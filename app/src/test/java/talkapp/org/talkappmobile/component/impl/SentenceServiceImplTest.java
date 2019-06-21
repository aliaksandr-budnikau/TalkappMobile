package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.component.impl.SentenceServiceImpl.WORDS_NUMBER;

@RunWith(MockitoJUnitRunner.class)
public class SentenceServiceImplTest {
    @Mock
    WordRepetitionProgressService exerciseService;
    @Mock
    private DataServer dataServer;
    @InjectMocks
    private SentenceServiceImpl sentenceService;

    @Test
    public void fetchSentencesFromServerByWordAndWordSetId() {
        // setup
        LinkedList<Sentence> sentences = new LinkedList<>();
        sentences.add(new Sentence());
        sentences.getLast().getTranslations().put("russian", "Текст 1");
        sentences.add(new Sentence());
        sentences.getLast().getTranslations().put("russian", "Текст 1");
        sentences.add(new Sentence());
        sentences.getLast().getTranslations().put("russian", "Текст 2");
        sentences.add(new Sentence());
        sentences.getLast().getTranslations().put("russian", "Текст 1");

        // when
        when(dataServer.findSentencesByWords(any(Word2Tokens.class), anyInt(), anyInt())).thenReturn(sentences);
        List<Sentence> result = sentenceService.fetchSentencesFromServerByWordAndWordSetId(new Word2Tokens(), 2);

        // then
        assertEquals(2, result.size());
        assertEquals("Текст 1", result.get(0).getTranslations().get("russian"));
        assertEquals("Текст 2", result.get(1).getTranslations().get("russian"));
    }


    @Test
    public void findByWord_sentenceFound() {
        // setup
        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens("word", "word", wordSetId);

        Sentence sentence1 = new Sentence();
        sentence1.setId("fds32ddd");
        sentence1.setTranslations(new HashMap<String, String>());
        sentence1.getTranslations().put("russian", "fsdfsdfs");
        Sentence sentence2 = new Sentence();
        sentence2.setId("fds32ddddsas");
        sentence2.setTranslations(new HashMap<String, String>());
        sentence2.getTranslations().put("russian", "fsdfsdfs2");
        List<Sentence> sentences = asList(sentence1, sentence2);

        // when
        when(dataServer.findSentencesByWords(word, WORDS_NUMBER, wordSetId)).thenReturn(sentences);
        List<Sentence> sentencesActual = sentenceService.fetchSentencesFromServerByWordAndWordSetId(word, wordSetId);

        // then
        assertEquals(sentences.get(0), sentencesActual.get(0));
        assertEquals("fds32ddd", sentencesActual.get(0).getId());
        assertEquals(sentences.get(1), sentencesActual.get(1));
        assertEquals("fds32ddddsas", sentencesActual.get(1).getId());
    }

    @Test
    public void findByWord_sentenceInImmutableList() {
        // setup
        Word2Tokens word = new Word2Tokens("word");
        int wordSetId = 3;

        Sentence sentence1 = new Sentence();
        sentence1.setId("fds32ddd");
        Sentence sentence2 = new Sentence();
        sentence2.setId("fds32ddddsas");
        List<Sentence> sentences = asList(sentence1, sentence2);

        // when
        when(dataServer.findSentencesByWords(word, WORDS_NUMBER, wordSetId)).thenReturn(sentences);
        List<Sentence> sentencesActual = sentenceService.fetchSentencesFromServerByWordAndWordSetId(word, wordSetId);

        // then
        sentencesActual.clear();
    }

    @Test
    public void findByWord_sentenceNotFound() {
        // setup
        Word2Tokens word = new Word2Tokens("word");
        int wordSetId = 3;

        List<Sentence> sentences = emptyList();

        // when
        when(dataServer.findSentencesByWords(word, WORDS_NUMBER, wordSetId)).thenReturn(sentences);
        List<Sentence> sentencesActual = sentenceService.fetchSentencesFromServerByWordAndWordSetId(word, wordSetId);

        // then
        assertTrue(sentencesActual.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void findByWord_exception() {
        // setup
        Word2Tokens word = new Word2Tokens("word");
        int wordSetId = 3;

        // when
        doThrow(RuntimeException.class).when(dataServer.findSentencesByWords(word, WORDS_NUMBER, wordSetId));
        List<Sentence> sentencesActual = sentenceService.fetchSentencesFromServerByWordAndWordSetId(word, wordSetId);

        // then
        assertTrue(sentencesActual.isEmpty());
    }

    @Test
    public void findByWordAndWordSetId() {
        // setup
        int wordSetId = 3;
        Word2Tokens word = new Word2Tokens("", "", wordSetId);

        // when
        when(exerciseService.findByWordAndWordSetId(word, wordSetId)).thenReturn(singletonList(new Sentence()));
        List<Sentence> list = sentenceService.fetchSentencesNotFromServerByWordAndWordSetId(word, wordSetId);

        // then
        list.clear();
    }
}