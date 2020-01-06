package talkapp.org.talkappmobile.activity.interactor;

import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnTopicsFragmentPresenterListener;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.service.TopicService;

public class TopicsFragmentInteractor {
    private final TopicService topicService;

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