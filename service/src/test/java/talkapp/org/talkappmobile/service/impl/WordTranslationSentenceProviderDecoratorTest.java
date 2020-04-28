package talkapp.org.talkappmobile.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.repository.WordTranslationRepository;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.WordTranslationSentenceProviderDecorator;

@RunWith(MockitoJUnitRunner.class)
public class WordTranslationSentenceProviderDecoratorTest {

    @Mock
    private SentenceProvider provider;
    @Mock
    private WordTranslationRepository wordTranslationRepository;
    @InjectMocks
    private WordTranslationSentenceProviderDecorator decorator;

    @Test
    public void find_unsupportedOperationException() {
        WordTranslation wordTranslation = new WordTranslation();
        wordTranslation.setWord("dfds");
        Mockito.when(wordTranslationRepository.findByWordAndByLanguage(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(wordTranslation);
        List<Sentence> sentences = decorator.find(new Word2Tokens("", null, null));
        Collections.sort(sentences, new Comparator<Sentence>() {
            @Override
            public int compare(Sentence o1, Sentence o2) {
                return 0;
            }
        });
        sentences.remove(0);
    }
}