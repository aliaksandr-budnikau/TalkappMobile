package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewHideAllStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewHideNewWordOnlyStrategy;
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
    public PracticeWordSetViewHideNewWordOnlyStrategy createPracticeWordSetViewHideNewWordOnlyStrategy(PracticeWordSetView view) {
        return new PracticeWordSetViewHideNewWordOnlyStrategy(view, textUtils, experienceUtils);
    }

    @Override
    public PracticeWordSetViewHideAllStrategy createPracticeWordSetViewHideAllStrategy(PracticeWordSetView view) {
        return new PracticeWordSetViewHideAllStrategy(view, textUtils, experienceUtils);
    }
}