package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.WordSetPracticeSectionsPagerAdapter;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetActivity extends BaseActivity {

    public static final String WORD_SET_MAPPING = "wordSet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WordSet wordSet = (WordSet) getIntent().getSerializableExtra(WORD_SET_MAPPING);

        setContentView(R.layout.activity_practice_word_set);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        WordSetPracticeSectionsPagerAdapter sectionsPagerAdapter = new WordSetPracticeSectionsPagerAdapter(getSupportFragmentManager(), wordSet);

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
    }
}