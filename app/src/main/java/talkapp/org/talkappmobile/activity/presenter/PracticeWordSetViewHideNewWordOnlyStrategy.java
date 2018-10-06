package talkapp.org.talkappmobile.activity.presenter;

import java.util.LinkedList;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;

public class PracticeWordSetViewHideNewWordOnlyStrategy extends PracticeWordSetViewStrategy {

    @Inject
    TextUtils textUtils;

    public PracticeWordSetViewHideNewWordOnlyStrategy(PracticeWordSetView view) {
        super(view);
        DIContext.get().inject(this);
    }

    @Override
    protected String hideRightAnswer(Sentence sentence, String word) {
        LinkedList<Integer> intervalsToHide = new LinkedList<>();
        for (TextToken token : sentence.getTokens()) {
            if (token.getToken().equals(word)) {
                intervalsToHide.add(token.getStartOffset());
                intervalsToHide.add(token.getEndOffset());
            }
        }
        return textUtils.hideIntervalsInText(sentence.getText(), intervalsToHide);
    }
}