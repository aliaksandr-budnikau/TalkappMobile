package talkapp.org.talkappmobile.component.database.dao;

import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.local.TopicMapping;

public interface TopicDao {
    List<TopicMapping> findAll();

    void save(List<TopicMapping> mappings);
}