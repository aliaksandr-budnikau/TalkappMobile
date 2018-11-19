package talkapp.org.talkappmobile.component.database.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.REPETITION;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.STUDYING;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetExerciseRepositoryImplTest {
    @Mock
    private PracticeWordSetExerciseDao exerciseDao;
    @Mock
    private WordSetExperienceDao experienceDao;
    @InjectMocks
    private PracticeWordSetExerciseRepositoryImpl repository;

    @Test
    public void isCurrentExerciseAnswered_trueInBeginning() {
        // setup
        int wordSetId = 3;

        WordSetExperienceMapping experienceMapping = new WordSetExperienceMapping();
        experienceMapping.setStatus(STUDYING);

        PracticeWordSetExerciseMapping exerciseMapping = new PracticeWordSetExerciseMapping();
        exerciseMapping.setStatus(REPETITION);

        // when
        when(experienceDao.findById(wordSetId)).thenReturn(experienceMapping);
        when(exerciseDao.findByCurrentAndByWordSetId(wordSetId)).thenReturn(singletonList(exerciseMapping));
        boolean answered = repository.isCurrentExerciseAnswered(wordSetId);

        // then
        assertTrue(answered);
    }

    @Test
    public void isCurrentExerciseAnswered_falseInBeginning() {
        // setup
        int wordSetId = 3;

        WordSetExperienceMapping experienceMapping = new WordSetExperienceMapping();
        experienceMapping.setStatus(STUDYING);

        PracticeWordSetExerciseMapping exerciseMapping = new PracticeWordSetExerciseMapping();
        exerciseMapping.setStatus(STUDYING);

        // when
        when(experienceDao.findById(wordSetId)).thenReturn(experienceMapping);
        when(exerciseDao.findByCurrentAndByWordSetId(wordSetId)).thenReturn(singletonList(exerciseMapping));
        boolean answered = repository.isCurrentExerciseAnswered(wordSetId);

        // then
        assertFalse(answered);
    }


    @Test
    public void isCurrentExerciseAnswered_trueInEnd() {
        // setup
        int wordSetId = 3;

        WordSetExperienceMapping experienceMapping = new WordSetExperienceMapping();
        experienceMapping.setStatus(REPETITION);

        PracticeWordSetExerciseMapping exerciseMapping = new PracticeWordSetExerciseMapping();
        exerciseMapping.setStatus(FINISHED);

        // when
        when(experienceDao.findById(wordSetId)).thenReturn(experienceMapping);
        when(exerciseDao.findByCurrentAndByWordSetId(wordSetId)).thenReturn(singletonList(exerciseMapping));
        boolean answered = repository.isCurrentExerciseAnswered(wordSetId);

        // then
        assertTrue(answered);
    }

    @Test
    public void isCurrentExerciseAnswered_falseInEnd() {
        // setup
        int wordSetId = 3;

        WordSetExperienceMapping experienceMapping = new WordSetExperienceMapping();
        experienceMapping.setStatus(REPETITION);

        PracticeWordSetExerciseMapping exerciseMapping = new PracticeWordSetExerciseMapping();
        exerciseMapping.setStatus(REPETITION);

        // when
        when(experienceDao.findById(wordSetId)).thenReturn(experienceMapping);
        when(exerciseDao.findByCurrentAndByWordSetId(wordSetId)).thenReturn(singletonList(exerciseMapping));
        boolean answered = repository.isCurrentExerciseAnswered(wordSetId);

        // then
        assertFalse(answered);
    }

    @Test
    public void isCurrentExerciseAnswered_noCurrent() {
        // setup
        int wordSetId = 3;

        WordSetExperienceMapping experienceMapping = new WordSetExperienceMapping();
        experienceMapping.setStatus(REPETITION);

        PracticeWordSetExerciseMapping exerciseMapping = new PracticeWordSetExerciseMapping();
        exerciseMapping.setStatus(REPETITION);

        // when
        when(experienceDao.findById(wordSetId)).thenReturn(experienceMapping);
        when(exerciseDao.findByCurrentAndByWordSetId(wordSetId)).thenReturn(Collections.<PracticeWordSetExerciseMapping>emptyList());
        boolean answered = repository.isCurrentExerciseAnswered(wordSetId);

        // then
        assertFalse(answered);
    }
}