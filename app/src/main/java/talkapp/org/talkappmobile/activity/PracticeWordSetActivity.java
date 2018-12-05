package talkapp.org.talkappmobile.activity;

import android.support.v4.view.ViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.PracticeWordSetPagerAdapter;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

@EActivity(R.layout.activity_practice_word_set)
public class PracticeWordSetActivity extends BaseActivity {

    public static final String TOPIC_MAPPING = "topic";
    public static final String WORD_SET_MAPPING = "wordSet";
    public static final String REPETITION_MODE_MAPPING = "repetitionMode";

    @ViewById(R.id.container)
    ViewPager viewPager;

    @Extra(TOPIC_MAPPING)
    Topic topic;
    @Extra(WORD_SET_MAPPING)
    WordSet wordSet;
    @Extra(REPETITION_MODE_MAPPING)
    boolean repetitionMode;

    @AfterViews
    public void init() {
        if (topic != null) {
            setTitle(topic.getName());
        }
        PracticeWordSetPagerAdapter sectionsPagerAdapter = new PracticeWordSetPagerAdapter(getSupportFragmentManager(), wordSet, repetitionMode);

        viewPager.setAdapter(sectionsPagerAdapter);
    }
}