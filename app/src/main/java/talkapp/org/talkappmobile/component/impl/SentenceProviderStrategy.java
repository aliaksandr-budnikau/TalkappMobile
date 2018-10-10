package talkapp.org.talkappmobile.component.impl;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.SentenceService;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;

public class SentenceProviderStrategy {
    public static final int WORDS_NUMBER = 6;
    @Inject
    SentenceService sentenceService;
    @Inject
    AuthSign authSign;

    public SentenceProviderStrategy() {
        DIContext.get().inject(this);
    }

    public List<Sentence> findByWordAndWordSetId(String word, String wordSetId) {
        try {
            return sentenceService.findByWords(word, WORDS_NUMBER, authSign).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}