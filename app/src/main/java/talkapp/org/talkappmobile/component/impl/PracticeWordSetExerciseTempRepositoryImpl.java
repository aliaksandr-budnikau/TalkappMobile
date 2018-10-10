package talkapp.org.talkappmobile.component.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import talkapp.org.talkappmobile.component.PracticeWordSetExerciseTempRepository;
import talkapp.org.talkappmobile.model.Sentence;

public class PracticeWordSetExerciseTempRepositoryImpl implements PracticeWordSetExerciseTempRepository {

    private Map<String, Sentence> cache = new LinkedHashMap<>();

    @Override
    public Sentence findByWordAndWordSetId(String word, String wordSetId) {
        return cache.get(word);
    }

    @Override
    public void save(String word, String wordSetId, Sentence sentence) {
        cache.put(word, sentence);
    }
}