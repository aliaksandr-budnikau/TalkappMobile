package talkapp.org.talkappmobile.service.impl;

import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;

public class CurrentPracticeStateServiceImpl implements CurrentPracticeStateService {
    private CurrentPracticeState currentPracticeState;

    @Override
    public CurrentPracticeState get() {
        return currentPracticeState;
    }

    @Override
    public void save(CurrentPracticeState currentPracticeState) {
        this.currentPracticeState = currentPracticeState;
    }
}
