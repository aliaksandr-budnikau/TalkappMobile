package talkapp.org.talkappmobile.service;

import java.util.LinkedList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.repository.TopicRepository;

@RequiredArgsConstructor
public class CachedTopicServiceDecorator implements TopicService {
    @Delegate(excludes = ExcludedMethods.class)
    private final TopicService service;
    private final TopicRepository topicRepository;

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
