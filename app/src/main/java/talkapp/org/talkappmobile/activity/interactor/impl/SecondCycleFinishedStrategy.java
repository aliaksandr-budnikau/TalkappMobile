package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;

class SecondCycleFinishedStrategy extends PracticeWordSetInteractorStrategy {
    private final WordRepetitionProgressService progressService;
    private final CurrentPracticeStateService currentPracticeStateService;

    SecondCycleFinishedStrategy(PracticeWordSetInteractor interactor, WordRepetitionProgressService progressService, CurrentPracticeStateService currentPracticeStateService) {
        super(interactor);
        this.progressService = progressService;
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        currentPracticeStateService.setStatus(FINISHED);
        currentPracticeStateService.persistWordSet();
        progressService.shiftSentences(currentPracticeStateService.getCurrentWord());
        listener.onTrainingFinished();
    }
}