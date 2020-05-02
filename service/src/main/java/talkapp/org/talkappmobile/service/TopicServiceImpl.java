package talkapp.org.talkappmobile.service;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.repository.TopicRepository;

public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final DataServer server;


    @Inject
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