package talkapp.org.talkappmobile.service.impl;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.mappings.TopicMapping;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.service.TopicRepository;

public class TopicRepositoryImpl implements TopicRepository {
    private final TopicDao topicDao;

    public TopicRepositoryImpl(TopicDao topicDao) {
        this.topicDao = topicDao;
    }

    @Override
    public List<Topic> findAll() {
        List<TopicMapping> all = topicDao.findAll();
        LinkedList<Topic> result = new LinkedList<>();
        for (TopicMapping mapping : all) {
            result.add(toDto(mapping));
        }
        return result;
    }

    private Topic toDto(TopicMapping mapping) {
        Topic topic = new Topic();
        topic.setId(mapping.getId());
        topic.setName(mapping.getName());
        return topic;
    }
}