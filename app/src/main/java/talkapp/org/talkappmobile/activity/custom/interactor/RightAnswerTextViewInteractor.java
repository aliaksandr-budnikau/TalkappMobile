package talkapp.org.talkappmobile.activity.custom.interactor;

import android.content.ActivityNotFoundException;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.TextToken;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.service.TextUtils;

import java.util.HashSet;
import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.custom.listener.OnRightAnswerTextViewListener;

import static java.util.Arrays.asList;

public class RightAnswerTextViewInteractor {
    private final TextUtils textUtils;

    public RightAnswerTextViewInteractor(TextUtils textUtils) {
        this.textUtils = textUtils;
    }

    public void maskEntirely(Sentence sentence, boolean locked, OnRightAnswerTextViewListener listener) {
        if (locked) {
            return;
        }
        String maskedValue = textUtils.screenTextWith(sentence.getText());
        listener.onNewValue(maskedValue);
    }

    public void maskOnlyWord(Sentence sentence, Word2Tokens word, boolean locked, OnRightAnswerTextViewListener listener) {
        if (locked) {
            return;
        }
        String maskedValue = maskOnlyWordAndPrepareIntervals(sentence, word);
        listener.onNewValue(maskedValue);
    }

    public void unmask(Sentence sentence, boolean locked, OnRightAnswerTextViewListener listener) {
        if (!locked) {
            String maskedValue = sentence.getText();
            listener.onNewValue(maskedValue);
            listener.onAnswerHasBeenSeen();
        }
    }

    private String maskOnlyWordAndPrepareIntervals(Sentence sentence, Word2Tokens word) {
        LinkedList<Integer> intervalsToHide = new LinkedList<>();
        for (TextToken token : sentence.getTokens()) {
            HashSet<String> tokens = new HashSet<>(asList(word.getTokens().split(",")));
            if (token.getToken().equals(word.getWord()) || tokens.contains(token.getToken())) {
                if (intervalsToHide.size() == 0 || intervalsToHide.getLast() < token.getStartOffset()) {
                    intervalsToHide.add(token.getStartOffset());
                    intervalsToHide.add(token.getEndOffset());
                }
            }
        }
        return textUtils.hideIntervalsInText(sentence.getText(), intervalsToHide);
    }

    public void openGoogleTranslate(Sentence sentence, boolean locked, OnRightAnswerTextViewListener listener) {
        if (locked) {
            try {
                listener.onOpenGoogleTranslate(sentence.getText(), "en", "ru");
            } catch (ActivityNotFoundException e) {
                listener.onActivityNotFoundException();
            }
        }
    }
}