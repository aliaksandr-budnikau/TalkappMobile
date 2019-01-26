package talkapp.org.talkappmobile.component.impl;

import java.util.List;

import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public class SentenceProviderImpl implements SentenceProvider {
    private final BackendSentenceProviderStrategy backendStrategy;
    private final SentenceProviderRepetitionStrategy repetitionStrategy;
    private SentenceProviderStrategy currentStrategy;

    public SentenceProviderImpl(BackendSentenceProviderStrategy backendStrategy, SentenceProviderRepetitionStrategy repetitionStrategy) {
        this.backendStrategy = backendStrategy;
        this.repetitionStrategy = repetitionStrategy;
        disableRepetitionMode();
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(Word2Tokens word, int wordSetId) {
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