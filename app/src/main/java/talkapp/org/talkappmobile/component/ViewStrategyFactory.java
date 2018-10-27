package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewHideAllStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewHideNewWordOnlyStrategy;

public interface ViewStrategyFactory {

    PracticeWordSetViewHideNewWordOnlyStrategy createPracticeWordSetViewHideNewWordOnlyStrategy(PracticeWordSetView view);

    PracticeWordSetViewHideAllStrategy createPracticeWordSetViewHideAllStrategy(PracticeWordSetView view);
}