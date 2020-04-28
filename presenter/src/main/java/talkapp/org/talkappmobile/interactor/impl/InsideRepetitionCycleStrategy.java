package talkapp.org.talkappmobile.interactor.impl;

import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;

class InsideRepetitionCycleStrategy extends PracticeWordSetInteractorStrategy {
    private final CurrentPracticeStateService currentPracticeStateService;

    public InsideRepetitionCycleStrategy(PracticeWordSetInteractor interactor, CurrentPracticeStateService currentPracticeStateService) {
        super(interactor);
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    void initialiseExperience(OnPracticeWordSetListener listener) {
        listener.onEnableRepetitionMode();
        currentPracticeStateService.setTrainingExperience(0);
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        listener.onRightAnswer(currentPracticeStateService.getCurrentSentence());
        currentPracticeStateService.addFinishedWord(currentPracticeStateService.getCurrentWord());
    }
}