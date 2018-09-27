package talkapp.org.talkappmobile.activity.adapter.impl;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import talkapp.org.talkappmobile.activity.adapter.AdaptersFactory;
import talkapp.org.talkappmobile.activity.adapter.TopicListAdapter;
import talkapp.org.talkappmobile.activity.adapter.WordSetListAdapter;
import talkapp.org.talkappmobile.activity.adapter.WordTranslationListAdapter;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

/**
 * @author Budnikau Aliaksandr
 */
public class AdaptersFactoryImpl implements AdaptersFactory {

    @Override
    public ArrayAdapter<WordSet> createWordSetListAdapter(@NonNull Context context) {
        return new WordSetListAdapter(context);
    }

    @Override
    public ArrayAdapter<Topic> createTopicListAdapter(@NonNull Context context) {
        return new TopicListAdapter(context);
    }

    @Override
    public ArrayAdapter<WordTranslation> createWordTranslationListAdapter(Context context) {
        return new WordTranslationListAdapter(context);
    }
}