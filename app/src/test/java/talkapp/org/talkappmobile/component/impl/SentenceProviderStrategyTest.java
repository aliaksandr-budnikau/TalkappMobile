package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.component.impl.SentenceProviderStrategy.WORDS_NUMBER;

@RunWith(MockitoJUnitRunner.class)
public class SentenceProviderStrategyTest {
    @Mock
    private BackendServer backendServer;
    @InjectMocks
    private SentenceProviderStrategy strategy;

    @Test
    public void findByWord_sentenceFound() {
        // setup
        Word2Tokens word = new Word2Tokens("word");
        int wordSetId = 3;

        Sentence sentence1 = new Sentence();
        sentence1.setId("fds32ddd");
        Sentence sentence2 = new Sentence();
        sentence2.setId("fds32ddddsas");
        List<Sentence> sentences = asList(sentence1, sentence2);

        // when
        when(backendServer.findSentencesByWords(word, WORDS_NUMBER)).thenReturn(sentences);
        List<Sentence> sentencesActual = strategy.findByWordAndWordSetId(word, wordSetId);

        // then
        assertEquals(sentences.get(0), sentencesActual.get(0));
        assertEquals("fds32ddd", sentencesActual.get(0).getId());
        assertEquals(sentences.get(1), sentencesActual.get(1));
        assertEquals("fds32ddddsas", sentencesActual.get(1).getId());
    }

    @Test
    public void findByWord_sentenceNotFound() {
        // setup
        Word2Tokens word = new Word2Tokens("word");
        int wordSetId = 3;

        List<Sentence> sentences = emptyList();

        // when
        when(backendServer.findSentencesByWords(word, WORDS_NUMBER)).thenReturn(sentences);
        List<Sentence> sentencesActual = strategy.findByWordAndWordSetId(word, wordSetId);

        // then
        assertTrue(sentencesActual.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void findByWord_exception() {
        // setup
        Word2Tokens word = new Word2Tokens("word");
        int wordSetId = 3;

        // when
        doThrow(RuntimeException.class).when(backendServer.findSentencesByWords(word, WORDS_NUMBER));
        List<Sentence> sentencesActual = strategy.findByWordAndWordSetId(word, wordSetId);

        // then
        assertTrue(sentencesActual.isEmpty());
    }
}