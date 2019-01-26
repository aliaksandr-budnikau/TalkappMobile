package talkapp.org.talkappmobile.component.impl;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public class SentenceProviderStrategy {
    public static final int WORDS_NUMBER = 6;
    private final DataServer server;

    public SentenceProviderStrategy(DataServer server) {
        this.server = server;
    }

    public List<Sentence> findByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        try {
            return new LinkedList<>(server.findSentencesByWords(word, WORDS_NUMBER, wordSetId));
        } catch (LocalCacheIsEmptyException e) {
            server.initLocalCacheOfAllSentencesForThisWordset(wordSetId, WORDS_NUMBER);
            List<Sentence> cached = server.findSentencesByWords(word, WORDS_NUMBER, wordSetId);
            return new LinkedList<>(cached);
        }
    }
}