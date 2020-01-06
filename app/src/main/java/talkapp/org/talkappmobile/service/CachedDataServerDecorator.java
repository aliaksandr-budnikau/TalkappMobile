package talkapp.org.talkappmobile.service;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.service.impl.InternetConnectionLostException;

public class CachedDataServerDecorator extends DataServerDecorator {

    private final TopicService topicService;

    public CachedDataServerDecorator(DataServer server, TopicService topicService) {
        super(server);
        this.topicService = topicService;
    }

    @Override
    public List<Topic> findAllTopics() {
        List<Topic> allTopics;
        try {
            allTopics = super.findAllTopics();
        } catch (InternetConnectionLostException e) {
            return topicService.findAllTopics();
        }
        if (allTopics == null) {
            return new LinkedList<>();
        } else {
            topicService.saveTopics(allTopics);
        }
        return allTopics;
    }

    public DataServer getServer() {
        return super.getServer();
    }
}