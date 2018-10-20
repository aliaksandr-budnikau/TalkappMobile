package talkapp.org.talkappmobile.component.impl;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.Sentence;

public class SentenceProviderImpl implements SentenceProvider {
    @Inject
    BackendSentenceProviderStrategy backendStrategy;
    @Inject
    SentenceProviderRepetitionStrategy repetitionStrategy;
    private SentenceProviderStrategy currentStrategy;

    public SentenceProviderImpl() {
        DIContextUtils.get().inject(this);
        disableRepetitionMode();
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(String word, String wordSetId) {
        return currentStrategy.findByWordAndWordSetId(word, wordSetId);
    }

    @Override
    public void enableRepetitionMode() {
        currentStrategy = repetitionStrategy;
    }

    @Override
    public void disableRepetitionMode() {
        currentStrategy = backendStrategy;
    }
}