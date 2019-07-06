package org.talkappmobile.activity.presenter;

import java.util.List;

import org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
import org.talkappmobile.activity.listener.OnTopicsFragmentPresenterListener;
import org.talkappmobile.activity.view.TopicsFragmentView;
import org.talkappmobile.model.Topic;

public class TopicsFragmentPresenter implements OnTopicsFragmentPresenterListener {
    private final TopicsFragmentView view;
    private final TopicsFragmentInteractor interactor;

    public TopicsFragmentPresenter(TopicsFragmentView view, TopicsFragmentInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }


    public void initialize() {
        try {
            view.onInitializeBeginning();
            interactor.loadTopics(this);
        } finally {
            view.onInitializeEnd();
        }
    }

    public void onTopicClick(Topic topic) {
        interactor.peekTopic(topic, this);
    }

    @Override
    public void onTopicsCame(List<Topic> topics) {
        view.setTopics(topics);
    }

    @Override
    public void onTopicChosen(Topic topic) {
        view.openTopicWordSetsFragment(topic);
    }
}