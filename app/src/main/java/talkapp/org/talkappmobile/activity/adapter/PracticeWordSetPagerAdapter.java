package talkapp.org.talkappmobile.activity.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import talkapp.org.talkappmobile.activity.PracticeWordSetFragment;
import talkapp.org.talkappmobile.activity.PracticeWordSetVocabularyFragment;
import talkapp.org.talkappmobile.model.WordSet;

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