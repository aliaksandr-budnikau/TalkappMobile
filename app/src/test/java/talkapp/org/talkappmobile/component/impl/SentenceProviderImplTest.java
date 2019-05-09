package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SentenceProviderImplTest {

    @Mock
    private BackendSentenceProviderStrategy backendStrategy;
    @Mock
    private SentenceProviderRepetitionStrategy repetitionStrategy;
    @InjectMocks
    private SentenceProviderImpl sentenceProvider;

    @Test
    public void findByWordAndWordSetId() {
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
        when(backendStrategy.findByWordAndWordSetId(any(Word2Tokens.class), anyInt())).thenReturn(sentences);
        List<Sentence> result = sentenceProvider.findByWordAndWordSetId(new Word2Tokens(), 2);

        // then
        assertEquals(2, result.size());
        assertEquals("Текст 1", result.get(0).getTranslations().get("russian"));
        assertEquals("Текст 2", result.get(1).getTranslations().get("russian"));
    }
}