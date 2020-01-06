package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;

public class CachedTopicServiceDecorator extends TopicServiceDecorator {
    public CachedTopicServiceDecorator(TopicService topicService) {
        super(topicService);
    }

    @Override
    public List<Topic> findAllTopics() {
        return super.findAllTopics();
    }
}
