package talkapp.org.talkappmobile.activity.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordTranslation;

/**
 * @author Budnikau Aliaksandr
 */
public interface AdaptersFactory {
    ArrayAdapter<Topic> createTopicListAdapter(Context context);

    ArrayAdapter<WordTranslation> createWordTranslationListAdapter(Context context);
}