package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.SentenceRepository;
import talkapp.org.talkappmobile.service.WordSetRepository;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WordRepetitionProgressServiceImplTest {
    public static final int COUNT = 10;
    @Mock
    private WordRepetitionProgressDao exerciseDao;
    @Mock
    private WordSetRepository wordSetRepository;
    @Mock
    private SentenceRepository sentenceRepository;
    private WordRepetitionProgressServiceImpl service;
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        service = new WordRepetitionProgressServiceImpl(exerciseDao, wordSetRepository, null, mapper);
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_emptyDB() {
        // setup
        long limit = 2;
        int olderThenInHours = 4;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        // when
        int wordSetSize = 12;
        when(exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), any(Date.class), any(String.class)))
                .thenReturn(Collections.<WordRepetitionProgressMapping>emptyList());
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(0, wordSets.size());
        ArgumentCaptor<Date> captor = forClass(Date.class);
        verify(exerciseDao).findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), captor.capture(), any(String.class));
        assertEquals(captor.getValue().getTime(), cal.getTime().getTime(), 100);
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyOneOldExercise() throws JsonProcessingException {
        // setup
        long limit = 2;
        int olderThenInHours = 4;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        // when
        ObjectMapper mapper = new ObjectMapper();

        int sourceWordSetId = 3;
        Word2Tokens value = new Word2Tokens("ddd", "sss", sourceWordSetId);

        LinkedList<WordRepetitionProgressMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.get(0).setId(1);
        expectedWordSets.get(0).setWordSetId(sourceWordSetId);
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());

        int wordSetSize = 1;
        Whitebox.setInternalState(service, "wordSetSize", wordSetSize);
        WordSet wordSet = new WordSet();
        wordSet.setId(sourceWordSetId);
        wordSet.setWords(asList(value));
        when(wordSetRepository.findById(sourceWordSetId)).thenReturn(wordSet);
        when(exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), any(Date.class), any(String.class)))
                .thenReturn(expectedWordSets);
        Whitebox.setInternalState(service, "mapper", mapper);
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(1, wordSets.size());
        assertEquals(expectedWordSets.get(0).getWordIndex(), 0);

        ArgumentCaptor<Date> captor = forClass(Date.class);
        verify(exerciseDao).findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), captor.capture(), any(String.class));
        assertEquals(captor.getValue().getTime(), cal.getTime().getTime(), 100);
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyOneOldExerciseWithRepetition0() throws JsonProcessingException {
        // setup
        long limit = 2;
        int olderThenInHours = 4;
        int sourceWordSetId = 3;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        Word2Tokens word2Tokens = new Word2Tokens("ddd", "sss", sourceWordSetId);

        WordSet wordSet = new WordSet();
        wordSet.setId(sourceWordSetId);
        wordSet.setWords(asList(word2Tokens));

        // when
        ObjectMapper mapper = new ObjectMapper();


        WordRepetitionProgressMapping progressMapping = new WordRepetitionProgressMapping();
        progressMapping.setWordSetId(sourceWordSetId);
        LinkedList<WordRepetitionProgressMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(progressMapping);
        expectedWordSets.get(0).setId(1);
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());

        int wordSetSize = 1;
        Whitebox.setInternalState(service, "wordSetSize", wordSetSize);
        when(wordSetRepository.findById(sourceWordSetId)).thenReturn(wordSet);
        when(exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), any(Date.class), any(String.class)))
                .thenReturn(expectedWordSets);
        Whitebox.setInternalState(service, "mapper", mapper);
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(1, wordSets.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_limitIsBig() throws JsonProcessingException {
        // setup
        long limit = 2;
        int olderThenInHours = 4;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        // when
        ObjectMapper mapper = new ObjectMapper();

        int sourceWordSetId = 3;
        Word2Tokens value1 = new Word2Tokens("ddd1", "sss1", sourceWordSetId);
        Word2Tokens value2 = new Word2Tokens("ddd2", "sss2", sourceWordSetId);
        Word2Tokens value3 = new Word2Tokens("ddd3", "sss3", sourceWordSetId);

        LinkedList<WordRepetitionProgressMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordSetId(sourceWordSetId);
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        expectedWordSets.getLast().setWordIndex(0);
        expectedWordSets.getLast().setRepetitionCounter(1);
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordSetId(sourceWordSetId);
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        expectedWordSets.getLast().setWordIndex(1);
        expectedWordSets.getLast().setRepetitionCounter(2);
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordSetId(sourceWordSetId);
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        expectedWordSets.getLast().setWordIndex(2);
        expectedWordSets.getLast().setRepetitionCounter(3);

        int wordSetSize = 2;
        Whitebox.setInternalState(service, "wordSetSize", wordSetSize);
        WordSet wordSet = new WordSet();
        wordSet.setId(sourceWordSetId);
        wordSet.setWords(asList(value1, value2, value3));
        when(wordSetRepository.findById(sourceWordSetId)).thenReturn(wordSet);
        when(exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), any(Date.class), any(String.class)))
                .thenReturn(expectedWordSets);
        Whitebox.setInternalState(service, "mapper", mapper);
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(2, wordSets.size());
        assertEquals(1, wordSets.get(1).getWords().size());

        ArgumentCaptor<Date> captor = forClass(Date.class);
        verify(exerciseDao).findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), captor.capture(), any(String.class));
        assertEquals(captor.getValue().getTime(), cal.getTime().getTime(), 100);
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_limitIsBigAndThereIsAlreadyRepeatedWord() throws JsonProcessingException {
        // setup
        long limit = 2;
        int olderThenInHours = 4;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        // when
        ObjectMapper mapper = new ObjectMapper();

        int sourceWordSetId = 3;
        Word2Tokens value1 = new Word2Tokens("ddd1", "sss1", sourceWordSetId);
        Word2Tokens value2 = new Word2Tokens("ddd2", "sss2", sourceWordSetId);
        Word2Tokens value3 = new Word2Tokens("ddd3", "sss3", sourceWordSetId);

        LinkedList<WordRepetitionProgressMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordSetId(sourceWordSetId);
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        cal.add(Calendar.MINUTE, 1);
        expectedWordSets.getLast().setWordIndex(0);
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordSetId(sourceWordSetId);
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        cal.add(Calendar.MINUTE, 1);
        expectedWordSets.getLast().setRepetitionCounter(1);
        expectedWordSets.getLast().setWordIndex(1);
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordSetId(sourceWordSetId);
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        cal.add(Calendar.MINUTE, 1);
        expectedWordSets.getLast().setWordIndex(2);

        int wordSetSize = 2;
        Whitebox.setInternalState(service, "wordSetSize", wordSetSize);
        WordSet wordSet = new WordSet();
        wordSet.setId(sourceWordSetId);
        wordSet.setWords(asList(value1, value2, value3));
        when(wordSetRepository.findById(sourceWordSetId)).thenReturn(wordSet);
        when(exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), any(Date.class), any(String.class))).thenReturn(new ArrayList<>(expectedWordSets));
        Whitebox.setInternalState(service, "mapper", mapper);
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(2, wordSets.size());
        assertEquals(1, wordSets.get(1).getWords().size());

        ArgumentCaptor<Date> captor = forClass(Date.class);
        verify(exerciseDao).findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), captor.capture(), any(String.class));
        assertEquals(captor.getValue().getTime(), cal.getTime().getTime(), 1000_000);
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_limitIsSmall() throws JsonProcessingException {
        // setup
        long limit = 1;
        int olderThenInHours = 4;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        // when
        ObjectMapper mapper = new ObjectMapper();
        int sourceWordSetId = 3;
        Word2Tokens value1 = new Word2Tokens("ddd1", "sss1", sourceWordSetId);
        Word2Tokens value2 = new Word2Tokens("ddd2", "sss2", sourceWordSetId);

        LinkedList<WordRepetitionProgressMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordSetId(sourceWordSetId);
        expectedWordSets.getLast().setWordIndex(0);
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordSetId(sourceWordSetId);
        expectedWordSets.getLast().setWordIndex(1);
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());

        int wordSetSize = 2;
        Whitebox.setInternalState(service, "wordSetSize", wordSetSize);
        WordSet wordSet = new WordSet();
        wordSet.setId(sourceWordSetId);
        wordSet.setWords(asList(value1, value2));
        when(wordSetRepository.findById(sourceWordSetId)).thenReturn(wordSet);
        when(exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), any(Date.class), any(String.class)))
                .thenReturn(expectedWordSets);
        Whitebox.setInternalState(service, "mapper", mapper);
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(1, wordSets.size());
        assertEquals(expectedWordSets.get(0).getWordIndex(), 0);
        assertEquals(expectedWordSets.get(1).getWordIndex(), 1);

        ArgumentCaptor<Date> captor = forClass(Date.class);
        verify(exerciseDao).findWordSetsSortByUpdatedDateAndByStatus(eq(limit * wordSetSize), captor.capture(), any(String.class));
        assertEquals(captor.getValue().getTime(), cal.getTime().getTime(), 100);
    }

    @Test
    public void findWordSetOfDifficultWords_failedAllTheTime() throws JsonProcessingException {
        int sourceWordSetId = 3;
        WordRepetitionProgressMapping word1 = new WordRepetitionProgressMapping();
        word1.setWordSetId(sourceWordSetId);
        Word2Tokens value1 = new Word2Tokens(null, null, sourceWordSetId);
        WordRepetitionProgressMapping word2 = new WordRepetitionProgressMapping();
        word2.setWordSetId(sourceWordSetId);
        Word2Tokens value2 = new Word2Tokens(null, null, sourceWordSetId);

        WordSet wordSet = new WordSet();
        wordSet.setId(sourceWordSetId);
        wordSet.setWords(asList(value1, value2));
        when(wordSetRepository.findById(sourceWordSetId)).thenReturn(wordSet);
        when(exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(anyLong(), any(Date.class), anyString())).thenReturn(asList(word1, word2));
        List<WordSet> sets = service.findWordSetOfDifficultWords();
        assertTrue(sets.isEmpty());
    }

    @Test
    public void findWordSetOfDifficultWords_12WordsWillNotBeDisplayed() throws JsonProcessingException {
        List<WordRepetitionProgressMapping> list = new LinkedList<>();
        int sourceWordSetId = 3;
        WordSet wordSet = new WordSet();
        wordSet.setId(sourceWordSetId);
        List<Word2Tokens> words = new LinkedList<>();
        for (int i = 0; i < 12; i++) {
            WordRepetitionProgressMapping word = new WordRepetitionProgressMapping();
            word.setWordSetId(sourceWordSetId);
            word.setWordIndex(i);
            Word2Tokens word2Tokens = new Word2Tokens(null, null, sourceWordSetId);
            words.add(word2Tokens);
            list.add(word);
        }
        wordSet.setWords(words);

        when(wordSetRepository.findById(sourceWordSetId)).thenReturn(wordSet);
        when(exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(anyLong(), any(Date.class), anyString())).thenReturn(list);
        List<WordSet> sets = service.findWordSetOfDifficultWords();
        assertEquals(1, sets.size());
        assertEquals(12, sets.get(0).getWords().size());
    }
}