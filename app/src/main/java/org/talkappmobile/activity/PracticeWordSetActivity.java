package org.talkappmobile.activity;

import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.KeyDown;
import org.androidannotations.annotations.ViewById;

import org.talkappmobile.R;
import org.talkappmobile.activity.adapter.PracticeWordSetPagerAdapter;
import org.talkappmobile.model.Topic;
import org.talkappmobile.model.WordSet;

import static android.view.KeyEvent.KEYCODE_BACK;

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

    @KeyDown({KEYCODE_BACK})
    void enterPressed(KeyEvent keyEvent) {
        finishActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void finishActivity() {
        if (!repetitionMode) {
            PracticeWordSetActivity.this.finish();
        } else {
            openDialog();
        }
    }

    private void openDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Are you leaving? Answered words will not be available for now.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        PracticeWordSetActivity.this.finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}