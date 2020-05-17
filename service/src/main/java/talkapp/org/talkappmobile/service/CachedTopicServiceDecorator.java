package talkapp.org.talkappmobile.service;

import java.util.LinkedList;
import java.util.List;

import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.repository.TopicRepository;

public class CachedTopicServiceDecorator implements TopicService {
    private final TopicRepository topicRepository;
    @Delegate(excludes = ExcludedMethods.class)
    private final TopicService service;

    public CachedTopicServiceDecorator(TopicService service, TopicRepository topicRepository) {
        this.service = service;
        this.topicRepository = topicRepository;
    }

    @Override
    public List<Topic> findAllTopics() {
        List<Topic> allTopics;
        try {
            allTopics = service.findAllTopics();
        } catch (InternetConnectionLostException e) {
            return topicRepository.findAll();
        }
        if (allTopics == null) {
            return new LinkedList<>();
        } else {
            service.saveTopics(allTopics);
        }
        return allTopics;
    }

    private interface ExcludedMethods {
        List<Topic> findAllTopics();
    }
}
