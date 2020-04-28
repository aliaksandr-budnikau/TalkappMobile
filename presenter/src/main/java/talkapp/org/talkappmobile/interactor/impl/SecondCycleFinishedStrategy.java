package talkapp.org.talkappmobile.interactor.impl;

import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

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
        currentPracticeStateService.setStatus(WordSetProgressStatus.FINISHED);
        currentPracticeStateService.persistWordSet();
        progressService.shiftSentences(currentPracticeStateService.getCurrentWord());
        listener.onTrainingFinished();
    }
}