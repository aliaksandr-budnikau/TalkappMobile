package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.presenter.OriginalTextTextViewPresenter;
import talkapp.org.talkappmobile.activity.custom.view.OriginalTextTextViewView;
import talkapp.org.talkappmobile.activity.event.wordset.ChangeSentenceOptionPickedEM;
import talkapp.org.talkappmobile.activity.event.wordset.ExerciseGotAnsweredEM;
import talkapp.org.talkappmobile.activity.event.wordset.NewSentenceEM;
import talkapp.org.talkappmobile.activity.event.wordset.OriginalTextClickEM;
import talkapp.org.talkappmobile.activity.event.wordset.PracticeHalfFinishedEM;
import talkapp.org.talkappmobile.activity.event.wordset.ScoreSentenceOptionPickedEM;
import talkapp.org.talkappmobile.activity.event.wordset.SentenceWasPickedForChangeEM;
import talkapp.org.talkappmobile.activity.event.wordset.SentencesWereFoundForChangeEM;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;

import static android.content.DialogInterface.BUTTON_POSITIVE;

@EView
public class OriginalTextTextView extends AppCompatTextView implements OriginalTextTextViewView {

    @EventBusGreenRobot
    EventBus eventBus;

    @StringRes(R.string.sentences_for_change_dialog_title)
    String sentencesForChangeDialogTitle;
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
    @StringRes(R.string.error_invalid_unchecking_all)
    String errorInvalidUncheckingAll;

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
    public void onMessageEvent(final SentencesWereFoundForChangeEM event) {
        presenter.prepareSentencesForPicking(event.getSentences(), event.getAlreadyPickedSentences());
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

    @Override
    public void openDialogForPickingNewSentence(final String[] options, final List<Sentence> sentences, boolean[] selectedOnes) {
        final boolean[] newSelectedOnes = new boolean[selectedOnes.length];
        System.arraycopy(selectedOnes, 0, newSelectedOnes, 0, selectedOnes.length);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(sentencesForChangeDialogTitle);
        setItemClickListner(options, selectedOnes, newSelectedOnes, builder);
        builder.setPositiveButton("OK", null);
        setButtonCancel(builder);
        setButtonNotOneAll(sentences, newSelectedOnes, builder);
        final AlertDialog alertDialog = builder.create();
        setButtonOK(sentences, newSelectedOnes, alertDialog);
        alertDialog.show();
    }

    private void setItemClickListner(String[] options, boolean[] selectedOnes, final boolean[] newSelectedOnes, AlertDialog.Builder builder) {
        builder.setMultiChoiceItems(options, selectedOnes, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                newSelectedOnes[which] = isChecked;
            }
        });
    }

    private void setButtonCancel(AlertDialog.Builder builder) {
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private void setButtonOK(final List<Sentence> sentences, final boolean[] newSelectedOnes, final AlertDialog alertDialog) {
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button ok = alertDialog.getButton(BUTTON_POSITIVE);
                ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinkedList<Sentence> result = new LinkedList<>();
                        for (int i = 0; i < newSelectedOnes.length; i++) {
                            if (newSelectedOnes[i]) {
                                result.add(sentences.get(i));
                            }
                        }
                        if (result.isEmpty()) {
                            Toast.makeText(getContext(), errorInvalidUncheckingAll, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        eventBus.post(new SentenceWasPickedForChangeEM(result));
                        alertDialog.cancel();
                    }
                });
            }
        });
    }

    private void setButtonNotOneAll(final List<Sentence> sentences, final boolean[] newSelectedOnes, AlertDialog.Builder builder) {
        builder.setNeutralButton("Not one/all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (boolean item : newSelectedOnes) {
                    if (item) {
                        presenter.prepareSentencesForPicking(sentences, Collections.<Sentence>emptyList());
                        return;
                    }
                }
                presenter.prepareSentencesForPicking(sentences, sentences);
            }
        });
    }
}