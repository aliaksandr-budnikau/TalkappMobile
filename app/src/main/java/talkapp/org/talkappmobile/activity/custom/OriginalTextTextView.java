package talkapp.org.talkappmobile.activity.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.event.wordset.ChangeSentenceOptionPickedEM;
import talkapp.org.talkappmobile.activity.event.wordset.ScoreSentenceOptionPickedEM;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;

@EView
public class OriginalTextTextView extends AppCompatTextView {

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
        options = new HashMap<>();
        options.put(SentenceContentScore.POOR, poorSentenceOption);
        options.put(SentenceContentScore.CORRUPTED, corruptedSentenceOption);
        options.put(SentenceContentScore.INSULT, insultSentenceOption);

    }

    @NonNull
    private String[] getOptions() {
        List<String> optionsList = new LinkedList<>();
        optionsList.add(anotherSentenceOption);
        for (SentenceContentScore value : SentenceContentScore.values()) {
            optionsList.add(options.get(value));
        }
        return optionsList.toArray(new String[SentenceContentScore.values().length]);
    }

    public void showOptionsInDialog(final Sentence sentence) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(scoreSentenceDialogTitle)
                .setItems(getOptions(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            eventBus.post(new ChangeSentenceOptionPickedEM());
                        } else {
                            eventBus.post(new ScoreSentenceOptionPickedEM(SentenceContentScore.values()[which - 1], sentence));
                        }
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
}