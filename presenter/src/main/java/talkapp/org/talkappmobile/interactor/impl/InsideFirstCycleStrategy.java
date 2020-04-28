package talkapp.org.talkappmobile.interactor.impl;

import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
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