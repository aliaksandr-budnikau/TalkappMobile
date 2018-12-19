package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetFirstCycleViewStrategy;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetSecondCycleViewStrategy;

public interface ViewStrategyFactory {

    PracticeWordSetFirstCycleViewStrategy createPracticeWordSetFirstCycleViewStrategy(PracticeWordSetView view);

    PracticeWordSetSecondCycleViewStrategy createPracticeWordSetSecondCycleViewStrategy(PracticeWordSetView view);
}