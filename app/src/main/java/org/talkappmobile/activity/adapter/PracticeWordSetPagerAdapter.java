package org.talkappmobile.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.talkappmobile.activity.PracticeWordSetFragment;
import org.talkappmobile.activity.PracticeWordSetVocabularyFragment;
import org.talkappmobile.model.WordSet;

public class PracticeWordSetPagerAdapter extends FragmentPagerAdapter {

    private final WordSet wordSet;
    private final boolean repetitionMode;

    public PracticeWordSetPagerAdapter(FragmentManager fm, WordSet wordSet, boolean repetitionMode) {
        super(fm);
        this.wordSet = wordSet;
        this.repetitionMode = repetitionMode;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return PracticeWordSetVocabularyFragment.newInstance(wordSet);
        } else {
            return PracticeWordSetFragment.newInstance(wordSet, repetitionMode);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}