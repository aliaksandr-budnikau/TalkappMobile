package talkapp.org.talkappmobile.activity;

import android.os.Bundle;

import talkapp.org.talkappmobile.model.Topic;

import static talkapp.org.talkappmobile.activity.WordSetsListFragment.TOPIC_MAPPING;

public class FragmentFactory {

    public static WordSetsListFragment createWordSetsListFragment(Topic topic) {
        Bundle args = new Bundle();
        args.putSerializable(TOPIC_MAPPING, topic);
        WordSetsListFragment fragment = new WordSetsListFragment_();
        fragment.setArguments(args);
        return fragment;
    }
}