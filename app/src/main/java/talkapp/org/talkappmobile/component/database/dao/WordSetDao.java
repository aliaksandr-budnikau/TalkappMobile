package talkapp.org.talkappmobile.component.database.dao;

import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.WordSetMapping;

public interface WordSetDao {
    List<WordSetMapping> findAll();

    void refreshAll(List<WordSetMapping> mappings);

    List<WordSetMapping> findAllByTopicId(String topicId);

    WordSetMapping findById(int id);

    void createNewOrUpdate(WordSetMapping wordSetMapping);

    Integer getTheLastCustomWordSetsId();

    void removeById(int id);
}