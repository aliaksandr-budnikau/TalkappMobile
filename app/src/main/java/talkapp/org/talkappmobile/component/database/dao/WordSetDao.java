package talkapp.org.talkappmobile.component.database.dao;

import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;

public interface WordSetDao {
    List<WordSetMapping> findAll();

    void save(List<WordSetMapping> mappings);

    List<WordSetMapping> findAllByTopicId(String topicId);
}