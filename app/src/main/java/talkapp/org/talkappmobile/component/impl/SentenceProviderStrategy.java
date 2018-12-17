package talkapp.org.talkappmobile.component.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

import static java.util.concurrent.TimeUnit.MINUTES;

public class SentenceProviderStrategy {
    public static final int WORDS_NUMBER = 6;
    private final BackendServer server;
    private final Cache<String, List<Sentence>> cache;

    public SentenceProviderStrategy(BackendServer server) {
        this.server = server;
        cache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(10, MINUTES)
                .build();
    }

    public List<Sentence> findByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        List<Sentence> cachedSentences = cache.getIfPresent(word.getWord());
        if (cachedSentences == null || cachedSentences.isEmpty()) {
            cachedSentences = server.findSentencesByWords(word, WORDS_NUMBER);
            cache.put(word.getWord(), cachedSentences);
        }
        return new ArrayList<>(cachedSentences);
    }
}