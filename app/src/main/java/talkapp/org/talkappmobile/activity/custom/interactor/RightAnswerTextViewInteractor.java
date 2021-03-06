package talkapp.org.talkappmobile.activity.custom.interactor;

import java.util.HashSet;
import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.custom.listener.OnRightAnswerTextViewListener;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.Word2Tokens;

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
}