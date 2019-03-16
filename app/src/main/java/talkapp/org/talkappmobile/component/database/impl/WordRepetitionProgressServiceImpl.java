package talkapp.org.talkappmobile.component.database.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.component.database.SentenceMapper;
import talkapp.org.talkappmobile.component.database.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

import static java.util.Calendar.getInstance;
import static java.util.Collections.emptyList;
import static okhttp3.internal.Util.UTC;
import static org.apache.commons.collections4.ListUtils.partition;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.next;

public class WordRepetitionProgressServiceImpl implements WordRepetitionProgressService {
    private final SentenceMapper sentenceMapper;
    private final SentenceDao sentenceDao;
    private WordRepetitionProgressDao exerciseDao;
    private WordSetDao wordSetDao;
    private ObjectMapper mapper;
    private int wordSetSize = 12;

    public WordRepetitionProgressServiceImpl(WordRepetitionProgressDao exerciseDao, WordSetDao wordSetDao, SentenceDao sentenceDao, ObjectMapper mapper) {
        this.exerciseDao = exerciseDao;
        this.wordSetDao = wordSetDao;
        this.sentenceDao = sentenceDao;
        this.mapper = mapper;
        this.sentenceMapper = new SentenceMapper(mapper);
    }

    @Override
    public Sentence findByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        List<WordRepetitionProgressMapping> exercises;
        try {
            exercises = exerciseDao.findByWordAndWordSetId(mapper.writeValueAsString(word), wordSetId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (exercises.isEmpty()) {
            return null;
        }
        return getSentence(exercises.get(0));
    }

    @Override
    public void save(Word2Tokens word, int wordSetId, Sentence sentence) {
        WordRepetitionProgressMapping exercise;
        try {
            exercise = exerciseDao.findByWordAndWordSetId(mapper.writeValueAsString(word), wordSetId).get(0);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        exercise.setSentenceId(sentence.getId());
        exercise.setUpdatedDate(getInstance(UTC).getTime());
        exerciseDao.createNewOrUpdate(exercise);
    }

    @Override
    public void cleanByWordSetId(int wordSetId) {
        exerciseDao.cleanByWordSetId(wordSetId);
    }

    @Override
    public void createSomeIfNecessary(Set<Word2Tokens> words, int wordSetId) {
        List<WordRepetitionProgressMapping> wordsEx = new LinkedList<>();
        for (Word2Tokens word : words) {
            List<WordRepetitionProgressMapping> alreadyCreatedWord = null;
            try {
                alreadyCreatedWord = exerciseDao.findByWordAndWordSetId(mapper.writeValueAsString(word), wordSetId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            if (alreadyCreatedWord != null && !alreadyCreatedWord.isEmpty()) {
                continue;
            }
            WordRepetitionProgressMapping exercise = new WordRepetitionProgressMapping();
            try {
                exercise.setWordJSON(mapper.writeValueAsString(word));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            exercise.setStatus(FIRST_CYCLE);
            exercise.setWordSetId(wordSetId);
            exercise.setUpdatedDate(getInstance(UTC).getTime());
            wordsEx.add(exercise);
        }
        exerciseDao.createAll(wordsEx);
    }

    @Override
    public Word2Tokens peekByWordSetIdAnyWord(int wordSetId) {
        WordSetMapping exp = wordSetDao.findById(wordSetId);
        List<WordRepetitionProgressMapping> exercises = exerciseDao.findByStatusAndByWordSetId(exp.getStatus(), wordSetId);
        int i = new Random().nextInt(exercises.size());
        WordRepetitionProgressMapping mapping = exercises.get(i);
        mapping.setCurrent(true);
        exerciseDao.createNewOrUpdate(mapping);
        try {
            return mapper.readValue(mapping.getWordJSON(), Word2Tokens.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Sentence getCurrentSentence(int wordSetId) {
        List<WordRepetitionProgressMapping> current = exerciseDao.findByCurrentAndByWordSetId(wordSetId);
        return getSentence(current.get(0));
    }

    @Override
    public List<WordSet> findFinishedWordSetsSortByUpdatedDate(int limit, int olderThenInHours) {
        Calendar cal = getInstance(UTC);
        //cal.add(Calendar.SECOND, -olderThenInHours);
        cal.add(Calendar.HOUR, -olderThenInHours);
        List<WordRepetitionProgressMapping> words = exerciseDao.findFinishedWordSetsSortByUpdatedDate(limit * wordSetSize, cal.getTime());
        Iterator<WordRepetitionProgressMapping> iterator = words.iterator();
        while (iterator.hasNext()) {
            WordRepetitionProgressMapping exe = iterator.next();
            cal = getInstance(UTC);
            // 5, 7, 13, 23, 37,
            //cal.add(Calendar.SECOND, -(olderThenInHours + 48 * exe.getRepetitionCounter() * exe.getRepetitionCounter()));
            cal.add(Calendar.HOUR, -(olderThenInHours + 48 * exe.getRepetitionCounter() * exe.getRepetitionCounter()));
            if (exe.getUpdatedDate().after(cal.getTime())) {
                iterator.remove();
            }
        }
        List<WordSet> wordSets = new LinkedList<>();
        for (List<WordRepetitionProgressMapping> exercises : partition(words, wordSetSize)) {
            WordSet set = new WordSet();
            set.setWords(new LinkedList<Word2Tokens>());
            for (WordRepetitionProgressMapping mapping : exercises) {
                try {
                    set.getWords().add(mapper.readValue(mapping.getWordJSON(), Word2Tokens.class));
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            wordSets.add(set);
        }
        return wordSets;
    }

    @Override
    public List<WordSet> findFinishedWordSetsSortByUpdatedDate(int olderThenInHours) {
        return findFinishedWordSetsSortByUpdatedDate(Integer.MAX_VALUE, olderThenInHours);
    }

    @Override
    public void putOffCurrentWord(int wordSetId) {
        List<WordRepetitionProgressMapping> current = exerciseDao.findByCurrentAndByWordSetId(wordSetId);
        if (isNotThereCurrentExercise(current)) {
            return;
        }
        WordRepetitionProgressMapping mapping = current.get(0);
        mapping.setCurrent(false);
        exerciseDao.createNewOrUpdate(mapping);
    }

    @Override
    public Word2Tokens getCurrentWord(int wordSetId) {
        List<WordRepetitionProgressMapping> current = exerciseDao.findByCurrentAndByWordSetId(wordSetId);
        if (isNotThereCurrentExercise(current)) {
            return null;
        }
        WordRepetitionProgressMapping mapping = current.get(0);
        try {
            return mapper.readValue(mapping.getWordJSON(), Word2Tokens.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void moveCurrentWordToNextState(int wordSetId) {
        List<WordRepetitionProgressMapping> current = exerciseDao.findByCurrentAndByWordSetId(wordSetId);
        WordRepetitionProgressMapping mapping = current.get(0);
        mapping.setStatus(next(mapping.getStatus()));
        mapping.setUpdatedDate(getInstance(UTC).getTime());
        exerciseDao.createNewOrUpdate(mapping);
    }

    @Override
    public List<Sentence> findByWordAndByStatus(Word2Tokens word, WordSetProgressStatus status) {
        List<WordRepetitionProgressMapping> exercises;
        try {
            exercises = exerciseDao.findByWordAndByStatus(mapper.writeValueAsString(word), status);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (exercises.isEmpty()) {
            return emptyList();
        }
        LinkedList<Sentence> sentences = new LinkedList<>();
        for (WordRepetitionProgressMapping exercise : exercises) {
            sentences.add(getSentence(exercise));
        }
        return sentences;
    }

    private Sentence getSentence(WordRepetitionProgressMapping exercise) {
        SentenceMapping mapping = sentenceDao.findById(exercise.getSentenceId());
        if (mapping == null) {
            throw new RuntimeException("Sentence wasn't found");
        }
        return sentenceMapper.toDto(mapping);
    }

    @Override
    public int markAsRepeated(Word2Tokens word, Sentence sentence) {
        List<WordRepetitionProgressMapping> exercises;
        try {
            exercises = exerciseDao.findByWordAndBySentenceAndByStatus(
                    mapper.writeValueAsString(word),
                    mapper.writeValueAsString(sentence),
                    FINISHED
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        WordRepetitionProgressMapping exercise = exercises.get(0);
        int counter = exercise.getRepetitionCounter();
        counter++;
        exercise.setRepetitionCounter(counter);
        exercise.setUpdatedDate(getInstance(UTC).getTime());
        exerciseDao.createNewOrUpdate(exercise);
        return counter;
    }

    private boolean isNotThereCurrentExercise(List<WordRepetitionProgressMapping> current) {
        return current.isEmpty();
    }
}