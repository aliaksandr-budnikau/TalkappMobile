package talkapp.org.talkappmobile.service.impl;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.repository.TopicRepository;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.TopicService;

public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final DataServer server;

    public TopicServiceImpl(TopicRepository topicRepository, DataServer server) {
        this.server = server;
        this.topicRepository = topicRepository;
    }

    @Override
    public void saveTopics(final List<Topic> topics) {
        for (Topic topic : topics) {
            topicRepository.createNewOrUpdate(topic);
        }
    }

    @Override
    public List<Topic> findAllTopics() {
        return server.findAllTopics();
    }
}