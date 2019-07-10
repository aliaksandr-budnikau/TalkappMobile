package talkapp.org.talkappmobile.dao;

import java.util.List;

import talkapp.org.talkappmobile.mappings.SentenceMapping;

public interface SentenceDao {
    void save(List<SentenceMapping> mappings);

    List<SentenceMapping> findAllByWord(String word, int wordsNumber);

    List<SentenceMapping> findAllByIds(String[] ids);
}