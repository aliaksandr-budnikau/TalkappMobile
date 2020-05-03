package talkapp.org.talkappmobile.interactor;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.listener.OnTopicsFragmentPresenterListener;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.service.TopicService;

public class TopicsFragmentInteractor {
    private final TopicService topicService;

    @Inject
    public TopicsFragmentInteractor(TopicService topicService) {
        this.topicService = topicService;
    }

    public void loadTopics(OnTopicsFragmentPresenterListener listener) {
        List<Topic> allTopics = topicService.findAllTopics();
        listener.onTopicsCame(allTopics);
    }

    public void peekTopic(Topic topic, OnTopicsFragmentPresenterListener listener) {
        listener.onTopicChosen(topic);
    }
}