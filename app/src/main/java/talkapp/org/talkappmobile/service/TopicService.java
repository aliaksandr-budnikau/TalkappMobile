package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;

public interface TopicService {

    void saveTopics(List<Topic> topics);

    List<Topic> findAllTopics();
}