package talkapp.org.talkappmobile.activity.interactor;

import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnTopicsFragmentPresenterListener;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.model.Topic;

public class TopicsFragmentInteractor {
    private final DataServer server;

    public TopicsFragmentInteractor(DataServer server) {
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