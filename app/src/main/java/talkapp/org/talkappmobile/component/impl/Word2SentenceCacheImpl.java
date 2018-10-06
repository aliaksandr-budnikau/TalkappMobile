package talkapp.org.talkappmobile.component.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import talkapp.org.talkappmobile.component.Word2SentenceCache;
import talkapp.org.talkappmobile.model.Sentence;

public class Word2SentenceCacheImpl implements Word2SentenceCache {

    private Map<String, Sentence> cache = new LinkedHashMap<>();

    @Override
    public Sentence findByWord(String word) {
        return cache.get(word);
    }

    @Override
    public void save(String word, Sentence sentence) {
        cache.put(word, sentence);
    }
}