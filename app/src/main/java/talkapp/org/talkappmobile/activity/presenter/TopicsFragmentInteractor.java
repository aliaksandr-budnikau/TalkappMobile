package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.model.Topic;

public class TopicsFragmentInteractor {
    private final BackendServer server;

    public TopicsFragmentInteractor(BackendServer server) {
        this.server = server;
    }

    public void loadTopics(OnTopicsFragmentPresenterListener listener) {
        List<Topic> allTopics = server.findAllTopics();
        listener.onTopicsCame(allTopics);
    }

    public void peekTopic(Topic topic, OnTopicsFragmentPresenterListener listener) {
        listener.onTopicChosen(topic);
    }
}