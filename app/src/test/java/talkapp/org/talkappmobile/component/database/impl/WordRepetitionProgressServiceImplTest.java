package talkapp.org.talkappmobile.component.database.impl;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.component.database.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

import static java.util.Calendar.HOUR;
import static java.util.Calendar.getInstance;
import static okhttp3.internal.Util.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;

@RunWith(MockitoJUnitRunner.class)
public class WordRepetitionProgressServiceImplTest {
    public static final int COUNT = 10;
    @Mock
    private WordRepetitionProgressDao exerciseDao;
    private WordRepetitionProgressServiceImpl service;

    @Before
    public void setup() {
        service = new WordRepetitionProgressServiceImpl(exerciseDao, null, null, new ObjectMapper());
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
                .thenReturn(Collections.<WordRepetitionProgressMapping>emptyList());
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

        LinkedList<WordRepetitionProgressMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.get(0).setId(1);
        expectedWordSets.get(0).setWordJSON(mapper.writeValueAsString(value));
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());

        int wordSetSize = 1;
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
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyOneOldExerciseWithRepetition0() throws JsonProcessingException {
        // setup
        long limit = 2;
        int olderThenInHours = 4;
        Calendar cal = getInstance(UTC);

        // when
        ObjectMapper mapper = new ObjectMapper();

        Word2Tokens value = new Word2Tokens();
        value.setWord("ddd");
        value.setTokens("sss");

        LinkedList<WordRepetitionProgressMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.get(0).setId(1);
        expectedWordSets.get(0).setWordJSON(mapper.writeValueAsString(value));
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());

        int wordSetSize = 1;
        Whitebox.setInternalState(service, "wordSetSize", wordSetSize);
        when(exerciseDao.findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), any(Date.class)))
                .thenReturn(expectedWordSets);
        Whitebox.setInternalState(service, "mapper", mapper);
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(0, wordSets.size());
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

        LinkedList<WordRepetitionProgressMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value1));
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value2));
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value3));
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());

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
        assertEquals(1, wordSets.get(1).getWords().size());

        ArgumentCaptor<Date> captor = forClass(Date.class);
        verify(exerciseDao).findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), captor.capture());
        assertEquals(captor.getValue().getTime(), cal.getTime().getTime(), 100);
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_limitIsBigAndThereIsAlreadyRepeatedWord() throws JsonProcessingException {
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

        LinkedList<WordRepetitionProgressMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value1));
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value2));
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        expectedWordSets.getLast().setRepetitionCounter(1);
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value3));
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());

        int wordSetSize = 2;
        Whitebox.setInternalState(service, "wordSetSize", wordSetSize);
        when(exerciseDao.findFinishedWordSetsSortByUpdatedDate(eq(limit * wordSetSize), any(Date.class)))
                .thenReturn(new ArrayList<>(expectedWordSets));
        Whitebox.setInternalState(service, "mapper", mapper);
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate((int) limit, olderThenInHours);

        // then
        assertEquals(2, wordSets.size());
        assertEquals(expectedWordSets.get(0).getWordJSON(), mapper.writeValueAsString(wordSets.get(0).getWords().get(0)));
        assertEquals(expectedWordSets.get(2).getWordJSON(), mapper.writeValueAsString(wordSets.get(0).getWords().get(1)));
        assertEquals(expectedWordSets.get(1).getWordJSON(), mapper.writeValueAsString(wordSets.get(1).getWords().get(0)));
        assertEquals(1, wordSets.get(1).getWords().size());

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

        LinkedList<WordRepetitionProgressMapping> expectedWordSets = new LinkedList<>();
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value1));
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());
        expectedWordSets.add(new WordRepetitionProgressMapping());
        expectedWordSets.getLast().setWordJSON(mapper.writeValueAsString(value2));
        expectedWordSets.getLast().setUpdatedDate(cal.getTime());

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

    @Test
    public void removeDuplicates_hasDuplicates() {
        // setup
        Word2Tokens word = new Word2Tokens();
        Sentence sentence = new Sentence();
        sentence.setId("id");

        LinkedList<WordRepetitionProgressMapping> words = new LinkedList<>();
        Calendar cal = getInstance(UTC);
        for (int i = 0; i < COUNT; i++) {
            words.addLast(new WordRepetitionProgressMapping());
            words.getLast().setRepetitionCounter(i);
            words.getLast().setWordJSON("setWordJSON");
            words.getLast().setStatus(FINISHED);
            words.getLast().setSentenceId("setSentenceId");
            words.getLast().setId(i);
            cal.add(HOUR, -4 * i);
            words.getLast().setUpdatedDate(cal.getTime());
        }
        WordRepetitionProgressMapping last = words.getLast();

        // when
        when(exerciseDao.findByWordAndBySentenceIdAndByStatus(anyString(), anyString(), any(WordSetProgressStatus.class))).thenReturn(words);
        service.removeDuplicates(word, sentence);

        // then
        ArgumentCaptor<Collection> captor = forClass(Collection.class);
        verify(exerciseDao).delete(captor.capture());
        Collection collection = captor.getValue();
        assertEquals(COUNT - 1, collection.size());
        assertFalse(collection.contains(last));
    }

    @Test
    public void removeDuplicates_hasDuplicatesAndEqualRepetitionCounter() {
        // setup
        Word2Tokens word = new Word2Tokens();
        Sentence sentence = new Sentence();
        sentence.setId("id");

        LinkedList<WordRepetitionProgressMapping> words = new LinkedList<>();
        Calendar cal = getInstance(UTC);
        for (int i = 0; i < COUNT; i++) {
            words.addLast(new WordRepetitionProgressMapping());
            words.getLast().setRepetitionCounter(0);
            words.getLast().setId(i);
            words.getLast().setWordJSON("setWordJSON");
            words.getLast().setStatus(FINISHED);
            words.getLast().setSentenceId("setSentenceId");
            cal.add(HOUR, -4 * i);
            words.getLast().setUpdatedDate(cal.getTime());
        }
        WordRepetitionProgressMapping last = words.getLast();

        // when
        when(exerciseDao.findByWordAndBySentenceIdAndByStatus(anyString(), anyString(), any(WordSetProgressStatus.class))).thenReturn(words);
        service.removeDuplicates(word, sentence);

        // then
        ArgumentCaptor<Collection> captor = forClass(Collection.class);
        verify(exerciseDao).delete(captor.capture());
        Collection collection = captor.getValue();
        assertEquals(COUNT - 1, collection.size());
        assertFalse(collection.contains(last));
    }
}