package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategy;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;

public interface ViewStrategyFactory {

    PracticeWordSetViewStrategy createPracticeWordSetViewStrategy(PracticeWordSetView view);
}