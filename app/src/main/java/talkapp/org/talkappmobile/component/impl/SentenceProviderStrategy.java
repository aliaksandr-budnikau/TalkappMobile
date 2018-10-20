package talkapp.org.talkappmobile.component.impl;

import java.io.IOException;
import java.util.List;

import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.SentenceService;
import talkapp.org.talkappmobile.model.Sentence;

public class SentenceProviderStrategy {
    public static final int WORDS_NUMBER = 6;
    private final SentenceService sentenceService;
    private final AuthSign authSign;

    public SentenceProviderStrategy(SentenceService sentenceService, AuthSign authSign) {
        this.sentenceService = sentenceService;
        this.authSign = authSign;
    }

    public List<Sentence> findByWordAndWordSetId(String word, String wordSetId) {
        try {
            return sentenceService.findByWords(word, WORDS_NUMBER, authSign).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}