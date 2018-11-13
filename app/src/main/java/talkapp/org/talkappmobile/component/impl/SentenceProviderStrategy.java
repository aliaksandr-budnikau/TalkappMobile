package talkapp.org.talkappmobile.component.impl;

import java.util.List;

import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public class SentenceProviderStrategy {
    public static final int WORDS_NUMBER = 6;
    private final BackendServer server;

    public SentenceProviderStrategy(BackendServer server) {
        this.server = server;
    }

    public List<Sentence> findByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        return server.findSentencesByWords(word, WORDS_NUMBER);
    }
}