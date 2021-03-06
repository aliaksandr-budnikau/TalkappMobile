package talkapp.org.talkappmobile.activity.listener;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;

public interface OnTopicsFragmentPresenterListener {
    void onTopicsCame(List<Topic> topics);

    void onTopicChosen(Topic topic);
}