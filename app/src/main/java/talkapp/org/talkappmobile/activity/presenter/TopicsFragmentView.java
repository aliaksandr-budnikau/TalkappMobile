package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;

public interface TopicsFragmentView {
    void setTopics(List<Topic> topics);

    void openTopicWordSetsFragment(Topic topic);
}