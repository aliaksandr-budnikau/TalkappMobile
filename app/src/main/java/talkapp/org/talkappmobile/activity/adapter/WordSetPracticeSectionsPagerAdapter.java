package talkapp.org.talkappmobile.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import talkapp.org.talkappmobile.activity.WordSetPracticeFragment;
import talkapp.org.talkappmobile.activity.PracticeWordSetVocabularyFragment;
import talkapp.org.talkappmobile.model.WordSet;

public class WordSetPracticeSectionsPagerAdapter extends FragmentPagerAdapter {

    private final WordSet wordSet;

    public WordSetPracticeSectionsPagerAdapter(FragmentManager fm, WordSet wordSet) {
        super(fm);
        this.wordSet = wordSet;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return PracticeWordSetVocabularyFragment.newInstance(wordSet);
        } else {
            return WordSetPracticeFragment.newInstance(wordSet);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}