package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;

class InsideFirstCycleStrategy extends PracticeWordSetInteractorStrategy {
    private final CurrentPracticeStateService currentPracticeStateService;

    InsideFirstCycleStrategy(PracticeWordSetInteractor interactor, CurrentPracticeStateService currentPracticeStateService) {
        super(interactor);
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        listener.onRightAnswer(currentPracticeStateService.getCurrentSentence());
    }
}