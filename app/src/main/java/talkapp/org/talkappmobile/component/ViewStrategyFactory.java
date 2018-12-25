package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetFirstCycleViewStrategy;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;

public interface ViewStrategyFactory {

    PracticeWordSetFirstCycleViewStrategy createPracticeWordSetFirstCycleViewStrategy(PracticeWordSetView view);
}