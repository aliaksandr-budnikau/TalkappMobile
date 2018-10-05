package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.SentenceService;
import talkapp.org.talkappmobile.model.Sentence;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static retrofit2.Response.success;
import static talkapp.org.talkappmobile.component.impl.SentenceProviderStrategy.WORDS_NUMBER;

@RunWith(MockitoJUnitRunner.class)
public class SentenceProviderStrategyTest {
    @Mock
    private SentenceService sentenceService;
    @Mock
    private AuthSign authSign;
    @InjectMocks
    private SentenceProviderStrategy strategy;

    @Test
    public void findByWord_sentenceFound() throws IOException {
        // setup
        String word = "word";

        Sentence sentence1 = new Sentence();
        sentence1.setId("fds32ddd");
        Sentence sentence2 = new Sentence();
        sentence2.setId("fds32ddddsas");
        List<Sentence> sentences = asList(sentence1, sentence2);

        // when
        whenSentenceServiceFindByWords(word, sentences, false);
        List<Sentence> sentencesActual = strategy.findByWord(word);

        // then
        assertEquals(sentences.get(0), sentencesActual.get(0));
        assertEquals("fds32ddd", sentencesActual.get(0).getId());
        assertEquals(sentences.get(1), sentencesActual.get(1));
        assertEquals("fds32ddddsas", sentencesActual.get(1).getId());
    }

    @Test
    public void findByWord_sentenceNotFound() throws IOException {
        // setup
        String word = "word";

        List<Sentence> sentences = emptyList();

        // when
        whenSentenceServiceFindByWords(word, sentences, false);
        List<Sentence> sentencesActual = strategy.findByWord(word);

        // then
        assertTrue(sentencesActual.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void findByWord_exception() throws IOException {
        // setup
        String word = "word";

        List<Sentence> sentences = emptyList();

        // when
        whenSentenceServiceFindByWords(word, sentences, true);
        List<Sentence> sentencesActual = strategy.findByWord(word);

        // then
        assertTrue(sentencesActual.isEmpty());
    }

    private void whenSentenceServiceFindByWords(String word, List<Sentence> sentences, boolean exception) throws IOException {
        Call call = mock(Call.class);
        if (exception) {
            when(call.execute()).thenThrow(new IOException());
        } else {
            when(call.execute()).thenReturn(success(sentences));
        }
        when(sentenceService.findByWords(word, WORDS_NUMBER, authSign)).thenReturn(call);
    }
}