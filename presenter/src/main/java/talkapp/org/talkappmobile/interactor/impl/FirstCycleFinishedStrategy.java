package talkapp.org.talkappmobile.interactor.impl;

import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;

class FirstCycleFinishedStrategy extends PracticeWordSetInteractorStrategy {
    private final CurrentPracticeStateService currentPracticeStateService;

    FirstCycleFinishedStrategy(PracticeWordSetInteractor interactor, CurrentPracticeStateService currentPracticeStateService) {
        super(interactor);
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        currentPracticeStateService.setStatus(WordSetProgressStatus.SECOND_CYCLE);
        currentPracticeStateService.persistWordSet();
        listener.onTrainingHalfFinished(currentPracticeStateService.getCurrentSentence());
        listener.onEnableRepetitionMode();
    }
}