package org.talkappmobile.activity.interactor;

import org.talkappmobile.model.Topic;
import org.talkappmobile.service.DataServer;

import java.util.List;

import org.talkappmobile.activity.listener.OnTopicsFragmentPresenterListener;

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