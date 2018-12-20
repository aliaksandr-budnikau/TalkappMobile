package talkapp.org.talkappmobile.activity.presenter;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;

public class PracticeWordSetSecondCycleViewStrategy extends PracticeWordSetViewStrategy {
    private final PracticeWordSetView view;

    public PracticeWordSetSecondCycleViewStrategy(PracticeWordSetView view, TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        super(view, textUtils, experienceUtils);
        this.view = view;
    }

    @Override
    public void hideRightAnswer() {
        view.maskRightAnswerEntirely();
    }

    @Override
    public void onSentenceChangeUnsupported() {
        view.showSentenceChangeUnsupportedMessage();
    }

    @Override
    public void onSentenceChanged() {
    }
}