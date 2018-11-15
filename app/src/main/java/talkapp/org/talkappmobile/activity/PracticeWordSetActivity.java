package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.PracticeWordSetPagerAdapter;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

public class PracticeWordSetActivity extends BaseActivity {

    public static final String TOPIC_MAPPING = "topic";
    public static final String WORD_SET_MAPPING = "wordSet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Topic topic = (Topic) getIntent().getSerializableExtra(TOPIC_MAPPING);
        if (topic != null) {
            setTitle(topic.getName());
        }

        WordSet wordSet = (WordSet) getIntent().getSerializableExtra(WORD_SET_MAPPING);

        setContentView(R.layout.activity_practice_word_set);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        PracticeWordSetPagerAdapter sectionsPagerAdapter = new PracticeWordSetPagerAdapter(getSupportFragmentManager(), wordSet);

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
    }
}