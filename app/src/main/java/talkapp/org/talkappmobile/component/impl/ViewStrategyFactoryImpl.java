package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategy;
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
    public PracticeWordSetViewStrategy createPracticeWordSetViewStrategy(PracticeWordSetView view) {
        return new PracticeWordSetViewStrategy(view, textUtils, experienceUtils);
    }
}