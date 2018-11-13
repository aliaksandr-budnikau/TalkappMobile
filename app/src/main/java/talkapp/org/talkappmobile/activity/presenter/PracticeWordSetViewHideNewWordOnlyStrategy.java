package talkapp.org.talkappmobile.activity.presenter;

import java.util.HashSet;
import java.util.LinkedList;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.Word2Tokens;

import static java.util.Arrays.asList;

public class PracticeWordSetViewHideNewWordOnlyStrategy extends PracticeWordSetViewStrategy {

    private final TextUtils textUtils;

    public PracticeWordSetViewHideNewWordOnlyStrategy(PracticeWordSetView view, TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        super(view, textUtils, experienceUtils);
        this.textUtils = textUtils;
    }

    @Override
    protected String hideRightAnswer(Sentence sentence, Word2Tokens word) {
        LinkedList<Integer> intervalsToHide = new LinkedList<>();
        for (TextToken token : sentence.getTokens()) {
            HashSet<String> tokens = new HashSet<>(asList(word.getTokens().split(",")));
            if (token.getToken().equals(word.getWord()) || tokens.contains(token.getToken())) {
                intervalsToHide.add(token.getStartOffset());
                intervalsToHide.add(token.getEndOffset());
            }
        }
        return textUtils.hideIntervalsInText(sentence.getText(), intervalsToHide);
    }
}