package talkapp.org.talkappmobile.activity.presenter;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;

public class PracticeWordSetViewHideNewWordOnlyStrategy extends PracticeWordSetViewStrategy {

    public PracticeWordSetViewHideNewWordOnlyStrategy(PracticeWordSetView view, TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        super(view, textUtils, experienceUtils);
    }

    @Override
    protected void hideRightAnswer(PracticeWordSetView view) {
        view.maskRightAnswerOnlyWord();
    }
}