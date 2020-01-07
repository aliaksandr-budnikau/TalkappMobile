package talkapp.org.talkappmobile.service;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.mappings.TopicMapping;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.service.impl.InternetConnectionLostException;

public class CachedTopicServiceDecorator extends TopicServiceDecorator {
    private final TopicDao topicDao;

    public CachedTopicServiceDecorator(TopicService topicService, TopicDao topicDao) {
        super(topicService);
        this.topicDao = topicDao;
    }

    @Override
    public List<Topic> findAllTopics() {
        List<Topic> allTopics;
        try {
            allTopics = super.findAllTopics();
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
            super.saveTopics(allTopics);
        }
        return allTopics;
    }

    private Topic toDto(TopicMapping mapping) {
        Topic topic = new Topic();
        topic.setId(mapping.getId());
        topic.setName(mapping.getName());
        return topic;
    }
}
