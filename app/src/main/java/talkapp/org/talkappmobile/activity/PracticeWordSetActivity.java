package talkapp.org.talkappmobile.activity;

import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_practice_word_set_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_scoring) {
            Toast.makeText(getApplicationContext(), "Scoring doesn't work still", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}