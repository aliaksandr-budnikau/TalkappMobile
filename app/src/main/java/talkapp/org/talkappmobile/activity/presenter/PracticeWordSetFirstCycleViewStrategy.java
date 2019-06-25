package talkapp.org.talkappmobile.activity.presenter;

import org.talkappmobile.service.TextUtils;
import org.talkappmobile.service.WordSetExperienceUtils;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;

public class PracticeWordSetFirstCycleViewStrategy extends PracticeWordSetViewStrategy {
    public PracticeWordSetFirstCycleViewStrategy(PracticeWordSetView view, TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        super(view, textUtils, experienceUtils);
    }
}