package talkapp.org.talkappmobile.activity.presenter;

import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;

public class PracticeWordSetViewHideNewWordOnlyStrategy extends PracticeWordSetViewStrategy {

    private final TextUtils textUtils;

    public PracticeWordSetViewHideNewWordOnlyStrategy(PracticeWordSetView view, TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        super(view, textUtils, experienceUtils);
        this.textUtils = textUtils;
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