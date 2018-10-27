package talkapp.org.talkappmobile.activity.view;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;

public interface TopicsFragmentView {
    void setTopics(List<Topic> topics);

    void openTopicWordSetsFragment(Topic topic);
}