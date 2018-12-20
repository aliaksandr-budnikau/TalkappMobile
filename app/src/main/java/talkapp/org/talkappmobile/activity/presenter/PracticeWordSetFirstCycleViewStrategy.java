package talkapp.org.talkappmobile.activity.presenter;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;

public class PracticeWordSetFirstCycleViewStrategy extends PracticeWordSetViewStrategy {

    private final PracticeWordSetView view;
    
    public PracticeWordSetFirstCycleViewStrategy(PracticeWordSetView view, TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        super(view, textUtils, experienceUtils);
        this.view = view;
    }

    @Override
    public void hideRightAnswer() {
        view.maskRightAnswerOnlyWord();
    }

    @Override
    public void onSentenceChangeUnsupported() {
    }

    @Override
    public void onSentenceChanged() {
        view.showSentenceChangedSuccessfullyMessage();
    }
}