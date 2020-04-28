package talkapp.org.talkappmobile.presenter;

import java.util.List;

import talkapp.org.talkappmobile.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.listener.OnTopicsFragmentPresenterListener;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.view.TopicsFragmentView;

public class TopicsFragmentPresenterImpl implements OnTopicsFragmentPresenterListener, TopicsFragmentPresenter {
    private final TopicsFragmentView view;
    private final TopicsFragmentInteractor interactor;

    public TopicsFragmentPresenterImpl(TopicsFragmentView view, TopicsFragmentInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void initialize() {
        try {
            view.onInitializeBeginning();
            interactor.loadTopics(this);
        } finally {
            view.onInitializeEnd();
        }
    }

    @Override
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