package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.model.Topic;

public interface TopicsFragmentPresenter {
    void initialize();

    void onTopicClick(Topic topic);
}
