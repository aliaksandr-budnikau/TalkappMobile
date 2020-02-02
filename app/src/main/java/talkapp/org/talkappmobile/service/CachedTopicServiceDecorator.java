package talkapp.org.talkappmobile.service;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.service.impl.InternetConnectionLostException;

public class CachedTopicServiceDecorator extends TopicServiceDecorator {
    private final TopicRepository topicRepository;

    public CachedTopicServiceDecorator(TopicService topicService, TopicRepository topicRepository) {
        super(topicService);
        this.topicRepository = topicRepository;
    }

    @Override
    public List<Topic> findAllTopics() {
        List<Topic> allTopics;
        try {
            allTopics = super.findAllTopics();
        } catch (InternetConnectionLostException e) {
            return topicRepository.findAll();
        }
        if (allTopics == null) {
            return new LinkedList<>();
        } else {
            super.saveTopics(allTopics);
        }
        return allTopics;
    }
}
