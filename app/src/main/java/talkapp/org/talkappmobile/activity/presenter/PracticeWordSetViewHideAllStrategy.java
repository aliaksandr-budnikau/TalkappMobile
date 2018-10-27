package talkapp.org.talkappmobile.activity.presenter;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;

public class PracticeWordSetViewHideAllStrategy extends PracticeWordSetViewStrategy {
    public PracticeWordSetViewHideAllStrategy(PracticeWordSetView view, TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        super(view, textUtils, experienceUtils);
    }
}