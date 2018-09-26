package talkapp.org.talkappmobile.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import talkapp.org.talkappmobile.activity.WordSetPracticeFragment;
import talkapp.org.talkappmobile.model.WordSet;

public class WordSetPracticeSectionsPagerAdapter extends FragmentPagerAdapter {

    private final WordSet wordSet;

    public WordSetPracticeSectionsPagerAdapter(FragmentManager fm, WordSet wordSet) {
        super(fm);
        this.wordSet = wordSet;
    }

    @Override
    public Fragment getItem(int position) {

        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return WordSetPracticeFragment.newInstance(wordSet);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }
}