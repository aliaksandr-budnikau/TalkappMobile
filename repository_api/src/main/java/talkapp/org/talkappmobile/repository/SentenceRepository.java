package talkapp.org.talkappmobile.repository;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;

public interface SentenceRepository {

    void createNewOrUpdate(Sentence sentence);

    Sentence findById(String id);

    List<Sentence> findAllByWord(String word, int wordsNumber);

    List<Sentence> findAllByIds(String[] ids);
}