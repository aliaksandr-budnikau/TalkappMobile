package talkapp.org.talkappmobile.component.database.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static java.util.Calendar.HOUR;
import static java.util.Calendar.getInstance;
import static java.util.Collections.singletonList;
import static okhttp3.internal.Util.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.REPETITION;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.STUDYING;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetExerciseServiceImplTest {
    @Mock
    private PracticeWordSetExerciseDao exerciseDao;
    @Mock
    private WordSetExperienceDao experienceDao;
    @InjectMocks
    private PracticeWordSetExerciseServiceImpl service;

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
        boolean answered = service.isCurrentExerciseAnswered(wordSetId);

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
        boolean answered = service.isCurrentExerciseAnswered(wordSetId);

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
        boolean answered = service.isCurrentExerciseAnswered(wordSetId);

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
        boolean answered = service.isCurrentExerciseAnswered(wordSetId);

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
        boolean answered = service.isCurrentExerciseAnswered(wordSetId);

        // then
        assertFalse(answered);
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_emptyDB() {
        // setup
        long limit = 2;
        int olderThenInHours = 4;
        Calendar cal = getInstance(UTC);
        cal.add(HOUR, -4);

        // when
        int wordSetSize = 12;
        when(exerciseDao.findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), any(Date.class)))
                .thenReturn(Collections.<PracticeWordSetExerciseMapping>emptyList());
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(0, wordSets.size());
        ArgumentCaptor<Date> captor = forClass(Date.class);
        verify(exerciseDao).findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), captor.capture());
        assertEquals(captor.getValue().getTime(), cal.getTime().getTime(), 100);
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyOneOldExercise() throws JsonProcessingException {
        // setup
        long limit = 2;
        int olderThenInHours = 4;
        Calendar cal = getInstance(UTC);
        cal.add(HOUR, -4);

        // when
        ObjectMapper mapper = new ObjectMapper();

        Word2Tokens value = new Word2Tokens();
        value.setWord("ddd");
        value.setTokens("sss");

        LinkedList<PracticeWordSetExerciseMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new PracticeWordSetExerciseMapping());
        expectedWordSets.get(0).setId(1);
        expectedWordSets.get(0).setWordJSON(mapper.writeValueAsString(value));

        int wordSetSize = 12;
        Whitebox.setInternalState(service, "wordSetSize", wordSetSize);
        when(exerciseDao.findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), any(Date.class)))
                .thenReturn(expectedWordSets);
        Whitebox.setInternalState(service, "mapper", mapper);
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(1, wordSets.size());
        assertEquals(expectedWordSets.get(0).getWordJSON(), mapper.writeValueAsString(wordSets.get(0).getWords().get(0)));

        ArgumentCaptor<Date> captor = forClass(Date.class);
        verify(exerciseDao).findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), captor.capture());
        assertEquals(captor.getValue().getTime(), cal.getTime().getTime(), 100);
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_limitIsBig() throws JsonProcessingException {
        // setup
        long limit = 2;
        int olderThenInHours = 4;
        Calendar cal = getInstance(UTC);
        cal.add(HOUR, -4);

        // when
        ObjectMapper mapper = new ObjectMapper();

        Word2Tokens value1 = new Word2Tokens();
        value1.setWord("ddd1");
        value1.setTokens("sss1");

        Word2Tokens value2 = new Word2Tokens();
        value2.setWord("ddd2");
        value2.setTokens("sss2");

        Word2Tokens value3 = new Word2Tokens();
        value3.setWord("ddd3");
        value3.setTokens("sss3");

        LinkedList<PracticeWordSetExerciseMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new PracticeWordSetExerciseMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value1));
        expectedWordSets.add(new PracticeWordSetExerciseMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value2));
        expectedWordSets.add(new PracticeWordSetExerciseMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value3));

        int wordSetSize = 2;
        Whitebox.setInternalState(service, "wordSetSize", wordSetSize);
        when(exerciseDao.findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), any(Date.class)))
                .thenReturn(expectedWordSets);
        Whitebox.setInternalState(service, "mapper", mapper);
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(2, wordSets.size());
        assertEquals(expectedWordSets.get(0).getWordJSON(), mapper.writeValueAsString(wordSets.get(0).getWords().get(0)));
        assertEquals(expectedWordSets.get(1).getWordJSON(), mapper.writeValueAsString(wordSets.get(0).getWords().get(1)));
        assertEquals(expectedWordSets.get(2).getWordJSON(), mapper.writeValueAsString(wordSets.get(1).getWords().get(0)));

        ArgumentCaptor<Date> captor = forClass(Date.class);
        verify(exerciseDao).findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), captor.capture());
        assertEquals(captor.getValue().getTime(), cal.getTime().getTime(), 100);
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_limitIsSmall() throws JsonProcessingException {
        // setup
        long limit = 1;
        int olderThenInHours = 4;
        Calendar cal = getInstance(UTC);
        cal.add(HOUR, -4);

        // when
        ObjectMapper mapper = new ObjectMapper();

        Word2Tokens value1 = new Word2Tokens();
        value1.setWord("ddd1");
        value1.setTokens("sss1");

        Word2Tokens value2 = new Word2Tokens();
        value2.setWord("ddd2");
        value2.setTokens("sss2");

        LinkedList<PracticeWordSetExerciseMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new PracticeWordSetExerciseMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value1));
        expectedWordSets.add(new PracticeWordSetExerciseMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value2));

        int wordSetSize = 2;
        Whitebox.setInternalState(service, "wordSetSize", wordSetSize);
        when(exerciseDao.findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), any(Date.class)))
                .thenReturn(expectedWordSets);
        Whitebox.setInternalState(service, "mapper", mapper);
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(1, wordSets.size());
        assertEquals(expectedWordSets.get(0).getWordJSON(), mapper.writeValueAsString(wordSets.get(0).getWords().get(0)));
        assertEquals(expectedWordSets.get(1).getWordJSON(), mapper.writeValueAsString(wordSets.get(0).getWords().get(1)));

        ArgumentCaptor<Date> captor = forClass(Date.class);
        verify(exerciseDao).findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), captor.capture());
        assertEquals(captor.getValue().getTime(), cal.getTime().getTime(), 100);
    }
}