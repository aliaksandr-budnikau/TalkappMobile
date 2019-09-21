package talkapp.org.talkappmobile.activity;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.adapter.PracticeWordSetPagerAdapter;
import talkapp.org.talkappmobile.events.ParentScreenOutdatedEM;
import talkapp.org.talkappmobile.events.WordSetPracticeFinishedEM;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

import static android.view.KeyEvent.KEYCODE_BACK;

@EActivity(R.layout.activity_practice_word_set)
public class PracticeWordSetActivity extends BaseActivity {

    public static final String TOPIC_MAPPING = "topic";
    public static final String WORD_SET_MAPPING = "wordSet";
    public static final String REPETITION_MODE_MAPPING = "repetitionMode";

    @EventBusGreenRobot
    EventBus eventBus;

    @ViewById(R.id.container)
    ViewPager viewPager;
    @ViewById(R.id.pagerTabStrip)
    PagerTabStrip pagerTabStrip;

    @Extra(TOPIC_MAPPING)
    Topic topic;
    @Extra(WORD_SET_MAPPING)
    WordSet wordSet;
    @Extra(REPETITION_MODE_MAPPING)
    boolean repetitionMode;

    @StringRes(R.string.activity_practice_word_set_tab_title_vocabulary)
    String vocabularyTabTitle;
    @StringRes(R.string.activity_practice_word_set_tab_title_practice)
    String practiceTabTitle;

    @AfterViews
    public void init() {
        if (topic != null) {
            setTitle(topic.getName());
        }
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorPrimaryLighter);
        PracticeWordSetPagerAdapter sectionsPagerAdapter = new PracticeWordSetPagerAdapter(getSupportFragmentManager(), wordSet, repetitionMode) {
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return vocabularyTabTitle;
                } else {
                    return practiceTabTitle;
                }
            }
        };

        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @KeyDown({KEYCODE_BACK})
    void enterPressed(KeyEvent keyEvent) {
        finishActivity();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetPracticeFinishedEM event) {
        finishCurrentActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void finishActivity() {
        if (!repetitionMode) {
            finishCurrentActivity();
        } else {
            openDialog();
        }
    }

    private void finishCurrentActivity() {
        eventBus.post(new ParentScreenOutdatedEM());
        PracticeWordSetActivity.this.finish();
    }

    private void openDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you leaving? Answered words will not be available for now.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finishCurrentActivity();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}