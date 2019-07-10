package talkapp.org.talkappmobile.dao;

import java.util.List;

import talkapp.org.talkappmobile.mappings.TopicMapping;

public interface TopicDao {
    List<TopicMapping> findAll();

    void save(List<TopicMapping> mappings);
}