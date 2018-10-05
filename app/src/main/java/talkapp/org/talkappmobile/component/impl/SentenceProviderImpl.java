package talkapp.org.talkappmobile.component.impl;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;

public class SentenceProviderImpl implements SentenceProvider {
    @Inject
    BackendSentenceProviderStrategy sentenceProviderStrategy;

    public SentenceProviderImpl() {
        DIContext.get().inject(this);
    }

    @Override
    public List<Sentence> findByWord(String word) {
        return sentenceProviderStrategy.findByWord(word);
    }
}