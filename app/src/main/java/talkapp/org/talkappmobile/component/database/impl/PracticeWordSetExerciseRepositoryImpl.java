package talkapp.org.talkappmobile.component.database.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.model.Sentence;

public class PracticeWordSetExerciseRepositoryImpl implements PracticeWordSetExerciseRepository {
    private PracticeWordSetExerciseDao exerciseDao;
    private ObjectMapper mapper;

    public PracticeWordSetExerciseRepositoryImpl(PracticeWordSetExerciseDao exerciseDao, ObjectMapper mapper) {
        this.exerciseDao = exerciseDao;
        this.mapper = mapper;
    }

    @Override
    public Sentence findByWordAndWordSetId(String word, String wordSetId) {
        List<PracticeWordSetExerciseMapping> exercises = exerciseDao.findByWordAndWordSetId(word, wordSetId);
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
    public void save(String word, String wordSetId, Sentence sentence) {
        PracticeWordSetExerciseMapping exercise = new PracticeWordSetExerciseMapping();
        exercise.setWord(word);
        try {
            exercise.setSentenceJSON(mapper.writeValueAsString(sentence));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        exercise.setWordSetId(wordSetId);
        exerciseDao.createNewOrUpdate(exercise);
    }

    @Override
    public void cleanByWordSetId(String wordSetId) {
        exerciseDao.cleanByWordSetId(wordSetId);
    }
}