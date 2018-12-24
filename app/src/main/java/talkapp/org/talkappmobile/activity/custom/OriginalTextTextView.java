package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.presenter.OriginalTextTextViewPresenter;
import talkapp.org.talkappmobile.activity.custom.view.OriginalTextTextViewView;
import talkapp.org.talkappmobile.activity.event.wordset.ChangeSentenceOptionPickedEM;
import talkapp.org.talkappmobile.activity.event.wordset.ExerciseGotAnsweredEM;
import talkapp.org.talkappmobile.activity.event.wordset.NewSentenceEM;
import talkapp.org.talkappmobile.activity.event.wordset.OriginalTextClickEM;
import talkapp.org.talkappmobile.activity.event.wordset.PracticeHalfFinishedEM;
import talkapp.org.talkappmobile.activity.event.wordset.ScoreSentenceOptionPickedEM;
import talkapp.org.talkappmobile.model.SentenceContentScore;

@EView
public class OriginalTextTextView extends AppCompatTextView implements OriginalTextTextViewView {

    @EventBusGreenRobot
    EventBus eventBus;

    @StringRes(R.string.score_sentence_dialog_title)
    String scoreSentenceDialogTitle;
    @StringRes(R.string.another_sentence_option)
    String anotherSentenceOption;
    @StringRes(R.string.poor_sentence_option)
    String poorSentenceOption;
    @StringRes(R.string.corrupted_sentence_option)
    String corruptedSentenceOption;
    @StringRes(R.string.insult_sentence_option)
    String insultSentenceOption;

    private Map<SentenceContentScore, String> options;
    private OriginalTextTextViewPresenter presenter;

    public OriginalTextTextView(Context context) {
        super(context);
    }

    public OriginalTextTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OriginalTextTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterInject
    public void init() {
        presenter = new OriginalTextTextViewPresenter(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewSentenceEM event) {
        presenter.setModel(event.getSentence());
        presenter.unlock();
        presenter.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ExerciseGotAnsweredEM event) {
        presenter.lock();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OriginalTextClickEM event) {
        presenter.prepareDialog(anotherSentenceOption, poorSentenceOption, corruptedSentenceOption, insultSentenceOption);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PracticeHalfFinishedEM event) {
        presenter.enableImmutableMode();
    }

    @Override
    public void setOriginalText(String originalText) {
        setText(originalText);
    }

    @Override
    public void onChangeSentence() {
        eventBus.post(new ChangeSentenceOptionPickedEM());
    }

    @Override
    public void openDialog(String[] options, final boolean mutable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder
                .setTitle(scoreSentenceDialogTitle)
                .setItems((options), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mutable) {
                            if (which == 0) {
                                presenter.changeSentence();
                                return;
                            }
                            which--;
                        }
                        eventBus.post(new ScoreSentenceOptionPickedEM(SentenceContentScore.values()[which], presenter.getSentence()));
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
}