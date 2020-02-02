package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;

public interface TopicRepository {
    List<Topic> findAll();
}