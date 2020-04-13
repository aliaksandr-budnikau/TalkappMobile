package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;

class TopicServiceDecorator implements TopicService {

    private final TopicService topicService;

    public TopicServiceDecorator(TopicService topicService) {
        this.topicService = topicService;
    }

    @Override
    public void saveTopics(List<Topic> topics) {
        topicService.saveTopics(topics);
    }

    @Override
    public List<Topic> findAllTopics() {
        return topicService.findAllTopics();
    }
}
