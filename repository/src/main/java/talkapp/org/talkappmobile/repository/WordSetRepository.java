package talkapp.org.talkappmobile.repository;

import java.util.List;

import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.WordSet;

public interface WordSetRepository {
    List<WordSet> findAll();

    List<WordSet> findAllByTopicId(int topicId);

    WordSet findById(int wordSetId);

    void createNewOrUpdate(WordSet wordSet);

    void createNewOrUpdate(List<WordSet> wordSets);

    void removeById(int wordSetId);

    Integer getTheLastCustomWordSetsId();

    NewWordSetDraft getNewWordSetDraft();

    void createNewOrUpdate(NewWordSetDraft draft);
}
