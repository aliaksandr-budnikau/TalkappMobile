package talkapp.org.talkappmobile.activity.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import talkapp.org.talkappmobile.model.WordSet;

/**
 * @author Budnikau Aliaksandr
 */
public interface AdaptersFactory {
    ArrayAdapter<WordSet> createWordSetListAdapter(Context context);
}