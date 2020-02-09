package talkapp.org.talkappmobile.repository;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;

public interface TopicRepository {
    List<Topic> findAll();

    void createNewOrUpdate(Topic topic);
}