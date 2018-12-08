package talkapp.org.talkappmobile.component.database.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static java.util.Calendar.getInstance;
import static java.util.Collections.emptyList;
import static okhttp3.internal.Util.UTC;
import static org.apache.commons.collections4.ListUtils.partition;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.STUDYING;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.next;

public class PracticeWordSetExerciseServiceImpl implements PracticeWordSetExerciseService {
    private PracticeWordSetExerciseDao exerciseDao;
    private WordSetExperienceDao experienceDao;
    private ObjectMapper mapper;
    private int wordSetSize = 12;

    public PracticeWordSetExerciseServiceImpl(PracticeWordSetExerciseDao exerciseDao, WordSetExperienceDao experienceDao, ObjectMapper mapper) {
        this.exerciseDao = exerciseDao;
        this.experienceDao = experienceDao;
        this.mapper = mapper;
    }

    @Override
    public Sentence findByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        List<PracticeWordSetExerciseMapping> exercises;
        try {
            exercises = exerciseDao.findByWordAndWordSetId(mapper.writeValueAsString(word), wordSetId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (exercises.isEmpty()) {
            return null;
        }
        PracticeWordSetExerciseMapping exercise = exercises.get(0);
        try {
            return mapper.readValue(exercise.getSentenceJSON(), Sentence.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void save(Word2Tokens word, int wordSetId, Sentence sentence) {
        PracticeWordSetExerciseMapping exercise;
        try {
            exercise = exerciseDao.findByWordAndWordSetId(mapper.writeValueAsString(word), wordSetId).get(0);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            exercise.setSentenceJSON(mapper.writeValueAsString(sentence));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        exercise.setUpdatedDate(getInstance(UTC).getTime());
        exerciseDao.createNewOrUpdate(exercise);
    }

    @Override
    public void cleanByWordSetId(int wordSetId) {
        exerciseDao.cleanByWordSetId(wordSetId);
    }

    @Override
    public void createSomeIfNecessary(Set<Word2Tokens> words, int wordSetId) {
        List<PracticeWordSetExerciseMapping> wordsEx = new LinkedList<>();
        for (Word2Tokens word : words) {
            List<PracticeWordSetExerciseMapping> alreadyCreatedWord = null;
            try {
                alreadyCreatedWord = exerciseDao.findByWordAndWordSetId(mapper.writeValueAsString(word), wordSetId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            if (alreadyCreatedWord != null && !alreadyCreatedWord.isEmpty()) {
                continue;
            }
            PracticeWordSetExerciseMapping exercise = new PracticeWordSetExerciseMapping();
            try {
                exercise.setWordJSON(mapper.writeValueAsString(word));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            exercise.setStatus(STUDYING);
            exercise.setWordSetId(wordSetId);
            exercise.setUpdatedDate(getInstance(UTC).getTime());
            wordsEx.add(exercise);
        }
        exerciseDao.createAll(wordsEx);
    }

    @Override
    public Word2Tokens peekByWordSetIdAnyWord(int wordSetId) {
        WordSetExperienceMapping exp = experienceDao.findById(wordSetId);
        List<PracticeWordSetExerciseMapping> exercises = exerciseDao.findByStatusAndByWordSetId(exp.getStatus(), wordSetId);
        PracticeWordSetExerciseMapping mapping = exercises.get(0);
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
        List<PracticeWordSetExerciseMapping> current = exerciseDao.findByCurrentAndByWordSetId(wordSetId);
        String sentenceJSON = current.get(0).getSentenceJSON();
        try {
            return mapper.readValue(sentenceJSON, Sentence.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<WordSet> findFinishedWordSetsSortByUpdatedDate(int limit, int olderThenInHours) {
        Calendar cal = getInstance(UTC);
        //cal.add(Calendar.SECOND, -olderThenInHours);
        cal.add(Calendar.HOUR, -olderThenInHours);
        List<PracticeWordSetExerciseMapping> words = exerciseDao.findFinishedWordSetsSortByUpdatedDate(limit * wordSetSize, cal.getTime());
        Iterator<PracticeWordSetExerciseMapping> iterator = words.iterator();
        while (iterator.hasNext()) {
            PracticeWordSetExerciseMapping exe = iterator.next();
            cal = getInstance(UTC);
            // 5, 7, 13, 23, 37,
            //cal.add(Calendar.SECOND, -(olderThenInHours + 48 * exe.getRepetitionCounter() * exe.getRepetitionCounter()));
            cal.add(Calendar.HOUR, -(olderThenInHours + 48 * exe.getRepetitionCounter() * exe.getRepetitionCounter()));
            if (exe.getUpdatedDate().after(cal.getTime())) {
                iterator.remove();
            }
        }
        List<WordSet> wordSets = new LinkedList<>();
        for (List<PracticeWordSetExerciseMapping> exercises : partition(words, wordSetSize)) {
            WordSet set = new WordSet();
            set.setWords(new LinkedList<Word2Tokens>());
            for (PracticeWordSetExerciseMapping mapping : exercises) {
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
        List<PracticeWordSetExerciseMapping> current = exerciseDao.findByCurrentAndByWordSetId(wordSetId);
        if (isNotThereCurrentExercise(current)) {
            return;
        }
        PracticeWordSetExerciseMapping mapping = current.get(0);
        mapping.setCurrent(false);
        exerciseDao.createNewOrUpdate(mapping);
    }

    @Override
    public void moveCurrentWordToNextState(int wordSetId) {
        List<PracticeWordSetExerciseMapping> current = exerciseDao.findByCurrentAndByWordSetId(wordSetId);
        PracticeWordSetExerciseMapping mapping = current.get(0);
        mapping.setStatus(next(mapping.getStatus()));
        mapping.setUpdatedDate(getInstance(UTC).getTime());
        exerciseDao.createNewOrUpdate(mapping);
    }

    @Override
    public List<Sentence> findByWordAndByStatus(Word2Tokens word, WordSetExperienceStatus status) {
        List<PracticeWordSetExerciseMapping> exercises;
        try {
            exercises = exerciseDao.findByWordAndByStatus(mapper.writeValueAsString(word), status);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (exercises.isEmpty()) {
            return emptyList();
        }
        LinkedList<Sentence> sentences = new LinkedList<>();
        for (PracticeWordSetExerciseMapping exercise : exercises) {
            try {
                sentences.add(mapper.readValue(exercise.getSentenceJSON(), Sentence.class));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return sentences;
    }

    @Override
    public void markAsRepeated(Word2Tokens word, Sentence sentence) {
        List<PracticeWordSetExerciseMapping> exercises;
        try {
            exercises = exerciseDao.findByWordAndBySentenceAndByStatus(
                    mapper.writeValueAsString(word),
                    mapper.writeValueAsString(sentence),
                    FINISHED
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        PracticeWordSetExerciseMapping exercise = exercises.get(0);
        int counter = exercise.getRepetitionCounter();
        counter++;
        exercise.setRepetitionCounter(counter);
        exercise.setUpdatedDate(getInstance(UTC).getTime());
        exerciseDao.createNewOrUpdate(exercise);
    }

    private boolean isNotThereCurrentExercise(List<PracticeWordSetExerciseMapping> current) {
        return current.isEmpty();
    }
}