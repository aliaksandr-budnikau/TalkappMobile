package talkapp.org.talkappmobile.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetFragment;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyFragment;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetPagerAdapter extends FragmentPagerAdapter {

    private final WordSet wordSet;

    public PracticeWordSetPagerAdapter(FragmentManager fm, WordSet wordSet) {
        super(fm);
        this.wordSet = wordSet;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return PracticeWordSetVocabularyFragment.newInstance(wordSet);
        } else {
            return PracticeWordSetFragment.newInstance(wordSet);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}