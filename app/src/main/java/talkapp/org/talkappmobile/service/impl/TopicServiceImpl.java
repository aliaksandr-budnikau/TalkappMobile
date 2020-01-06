package talkapp.org.talkappmobile.service.impl;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.mappings.TopicMapping;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.TopicService;

public class TopicServiceImpl implements TopicService {

    private final TopicDao topicDao;
    private final DataServer server;

    public TopicServiceImpl(TopicDao topicDao, DataServer server) {
        this.server = server;
        this.topicDao = topicDao;
    }

    @Override
    public void saveTopics(final List<Topic> topics) {
        LinkedList<TopicMapping> mappings = new LinkedList<>();
        for (Topic topic : topics) {
            mappings.add(toMapping(topic));
        }
        topicDao.save(mappings);
    }

    @Override
    public List<Topic> findAllTopics() {
        List<Topic> allTopics;
        try {
            allTopics = server.findAllTopics();
        } catch (InternetConnectionLostException e) {
            LinkedList<Topic> result = new LinkedList<>();
            for (TopicMapping mapping : topicDao.findAll()) {
                result.add(toDto(mapping));
            }
            return result;
        }
        if (allTopics == null) {
            return new LinkedList<>();
        } else {
            saveTopics(allTopics);
        }
        return allTopics;
    }

    private TopicMapping toMapping(Topic topic) {
        TopicMapping mapping = new TopicMapping();
        mapping.setId(topic.getId());
        mapping.setName(topic.getName());
        return mapping;
    }

    private Topic toDto(TopicMapping mapping) {
        Topic topic = new Topic();
        topic.setId(mapping.getId());
        topic.setName(mapping.getName());
        return topic;
    }
}