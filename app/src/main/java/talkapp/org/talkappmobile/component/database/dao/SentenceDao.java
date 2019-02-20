package talkapp.org.talkappmobile.component.database.dao;

import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping;

public interface SentenceDao {
    void save(List<SentenceMapping> mappings);

    List<SentenceMapping> findAllByWord(String word, int wordsNumber);

    SentenceMapping findById(String id);
}