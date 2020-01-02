package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

class FirstCycleFinishedStrategy extends PracticeWordSetInteractorStrategy {
    private final CurrentPracticeStateService currentPracticeStateService;

    FirstCycleFinishedStrategy(PracticeWordSetInteractor interactor, CurrentPracticeStateService currentPracticeStateService) {
        super(interactor);
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        currentPracticeStateService.setStatus(SECOND_CYCLE);
        currentPracticeStateService.persistWordSet();
        listener.onTrainingHalfFinished(currentPracticeStateService.getCurrentSentence());
        listener.onEnableRepetitionMode();
    }
}