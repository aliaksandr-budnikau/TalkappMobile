package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.CurrentPracticeState;

public interface CurrentPracticeStateService {
    CurrentPracticeState get();

    void save(CurrentPracticeState currentPracticeState);
}
