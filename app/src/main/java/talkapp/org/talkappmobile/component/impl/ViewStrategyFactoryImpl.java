package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetFirstCycleViewStrategy;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.ViewStrategyFactory;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;

public class ViewStrategyFactoryImpl implements ViewStrategyFactory {
    private final TextUtils textUtils;
    private final WordSetExperienceUtils experienceUtils;

    public ViewStrategyFactoryImpl(TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        this.textUtils = textUtils;
        this.experienceUtils = experienceUtils;
    }

    @Override
    public PracticeWordSetFirstCycleViewStrategy createPracticeWordSetFirstCycleViewStrategy(PracticeWordSetView view) {
        return new PracticeWordSetFirstCycleViewStrategy(view, textUtils, experienceUtils);
    }
}