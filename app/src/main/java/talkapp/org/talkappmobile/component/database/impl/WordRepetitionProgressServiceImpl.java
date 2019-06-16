package talkapp.org.talkappmobile.component.database.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import talkapp.org.talkappmobile.component.database.SentenceMapper;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.util.Calendar.getInstance;
import static java.util.Collections.emptyList;
import static okhttp3.internal.Util.UTC;
import static org.apache.commons.lang3.StringUtils.isEmpty;
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
    public List<Sentence> findByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        List<WordRepetitionProgressMapping> exercises;
        try {
            exercises = exerciseDao.findByWordAndWordSetId(mapper.writeValueAsString(word), wordSetId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (exercises.isEmpty()) {
            return emptyList();
        }
        WordRepetitionProgressMapping exercise = exercises.get(0);
        if (isEmpty(exercise.getSentenceIds())) {
            return emptyList();
        }
        return getSentence(exercise);
    }

    @Override
    public void save(Word2Tokens word, int wordSetId, List<Sentence> sentences) {
        WordRepetitionProgressMapping exercise;
        try {
            exercise = exerciseDao.findByWordAndWordSetId(mapper.writeValueAsString(word), wordSetId).get(0);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        joinSentenceIds(sentences, exercise);
        exercise.setUpdatedDate(getInstance(UTC).getTime());
        exerciseDao.createNewOrUpdate(exercise);
    }

    private void joinSentenceIds(List<Sentence> sentences, WordRepetitionProgressMapping exercise) {
        exercise.setSentenceIds("");
        for (Sentence sentence : sentences) {
            exercise.setSentenceIds(exercise.getSentenceIds() + (exercise.getSentenceIds().equals("") ? sentence.getId() : "," + sentence.getId()));
        }
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
    public void markNewCurrentWordByWordSetIdAndWord(int wordSetId, Word2Tokens newCurrentWord) {
        WordSetMapping exp = wordSetDao.findById(wordSetId);
        List<WordRepetitionProgressMapping> exercises = exerciseDao.findByStatusAndByWordSetId(exp.getStatus(), wordSetId);
        String newCurrentWordAsString;
        try {
            newCurrentWordAsString = mapper.writeValueAsString(newCurrentWord);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        for (WordRepetitionProgressMapping exercise : exercises) {
            if (exercise.getWordJSON().equals(newCurrentWordAsString)) {
                exercise.setCurrent(true);
                exerciseDao.createNewOrUpdate(exercise);
                return;
            }
        }
    }

    @Override
    public List<Word2Tokens> getLeftOverOfWordSetByWordSetId(int wordSetId) {
        WordSetMapping exp = wordSetDao.findById(wordSetId);
        List<WordRepetitionProgressMapping> exercises = exerciseDao.findByStatusAndByWordSetId(exp.getStatus(), wordSetId);
        LinkedList<Word2Tokens> result = new LinkedList<>();
        for (WordRepetitionProgressMapping exercise : exercises) {
            try {
                result.add(mapper.readValue(exercise.getWordJSON(), Word2Tokens.class));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return result;
    }

    @Override
    public List<WordSet> findFinishedWordSetsSortByUpdatedDate(long limit, int olderThenInHours) {
        Calendar cal = getInstance(UTC);
        //cal.add(Calendar.SECOND, -olderThenInHours);
        cal.add(Calendar.HOUR, -olderThenInHours);
        List<WordRepetitionProgressMapping> exercises = exerciseDao.findFinishedWordSetsSortByUpdatedDate(limit * wordSetSize, cal.getTime());
        Iterator<WordRepetitionProgressMapping> iterator = exercises.iterator();
        while (iterator.hasNext()) {
            WordRepetitionProgressMapping exe = iterator.next();
            cal = getInstance(UTC);
            // 5, 7, 13, 23, 37,
            //cal.add(Calendar.SECOND, -countHours(olderThenInHours, exe.getRepetitionCounter()));
            cal.add(Calendar.HOUR, -(countHours(olderThenInHours, exe.getRepetitionCounter())));
            if (exe.getUpdatedDate().after(cal.getTime())) {
                iterator.remove();
            }
        }
        NavigableMap<Integer, List<Word2Tokens>> tree = getSortedTreeByRepetitionCount(exercises);
        List<WordSet> wordSets = new LinkedList<>();
        for (RepetitionClass clazz : RepetitionClass.values()) {
            insertWordSetGroup(wordSets, tree.subMap(clazz.getFrom(), clazz.getTo()).values(), clazz);
        }
        return wordSets;
    }

    @NonNull
    private NavigableMap<Integer, List<Word2Tokens>> getSortedTreeByRepetitionCount(List<WordRepetitionProgressMapping> exercises) {
        NavigableMap<Integer, List<Word2Tokens>> tree = new TreeMap<>();
        for (WordRepetitionProgressMapping exercise : exercises) {
            Word2Tokens word2Tokens;
            try {
                word2Tokens = mapper.readValue(exercise.getWordJSON(), Word2Tokens.class);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            if (word2Tokens.getSourceWordSetId() == null) {
                word2Tokens.setSourceWordSetId(exercise.getWordSetId());
            }
            if (tree.get(exercise.getRepetitionCounter()) == null) {
                tree.put(exercise.getRepetitionCounter(), new LinkedList<Word2Tokens>());
            }
            tree.get(exercise.getRepetitionCounter()).add(word2Tokens);
        }
        return tree;
    }

    private void insertWordSetGroup(List<WordSet> allWordSets, Collection<List<Word2Tokens>> words, RepetitionClass clazz) {
        LinkedList<Word2Tokens> flattenList = new LinkedList<>();
        for (List<Word2Tokens> list : words) {
            flattenList.addAll(list);
        }
        WordSet set = new WordSet();
        set.setWords(new LinkedList<Word2Tokens>());
        set.setRepetitionClass(clazz);
        for (Word2Tokens word : flattenList) {
            set.getWords().add(word);
            if (set.getWords().size() == wordSetSize) {
                allWordSets.add(set);
                set = new WordSet();
                set.setWords(new LinkedList<Word2Tokens>());
                set.setRepetitionClass(clazz);
            }
        }
        if (!set.getWords().isEmpty()) {
            allWordSets.add(set);
        }
    }

    @Override
    public int getMaxWordSetSize() {
        return wordSetSize;
    }

    private int countHours(int olderThenInHours, int counter) {
        return (int) (olderThenInHours + log(max(counter, 1)) * 48 * counter);
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
            sentences.addAll(getSentence(exercise));
        }
        return sentences;
    }

    private List<Sentence> getSentence(WordRepetitionProgressMapping exercise) {
        String sentenceIds = exercise.getSentenceIds();
        List<SentenceMapping> sentences = sentenceDao.findAllByIds(sentenceIds.split(","));
        if (sentences.isEmpty()) {
            throw new RuntimeException("Sentence wasn't found");
        }
        LinkedList<Sentence> result = new LinkedList<>();
        for (SentenceMapping sentence : sentences) {
            result.add(sentenceMapper.toDto(sentence));
        }
        return result;
    }

    @Override
    public int markAsRepeated(Word2Tokens word, Sentence sentence) {
        WordRepetitionProgressMapping exercise = getWordRepetitionProgressMapping(word, sentence);
        int counter = exercise.getRepetitionCounter();
        counter++;
        exercise.setRepetitionCounter(counter);
        exercise.setUpdatedDate(getInstance(UTC).getTime());
        exerciseDao.createNewOrUpdate(exercise);
        return counter;
    }

    @Override
    public int markAsForgottenAgain(Word2Tokens word, Sentence sentence) {
        WordRepetitionProgressMapping exercise = getWordRepetitionProgressMapping(word, sentence);
        int counter = exercise.getForgettingCounter();
        counter++;
        exercise.setForgettingCounter(counter);
        exerciseDao.createNewOrUpdate(exercise);
        return counter;
    }

    private WordRepetitionProgressMapping getWordRepetitionProgressMapping(Word2Tokens word, Sentence sentence) {
        List<WordRepetitionProgressMapping> exercises;
        try {
            exercises = exerciseDao.findByWordAndByWordSetIdAndByStatus(
                    mapper.writeValueAsString(word),
                    word.getSourceWordSetId(),
                    FINISHED
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return exercises.get(0);
    }

    private boolean isNotThereCurrentExercise(List<WordRepetitionProgressMapping> current) {
        return current.isEmpty();
    }
}