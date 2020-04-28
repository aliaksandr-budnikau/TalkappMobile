package talkapp.org.talkappmobile.interactor.impl;

import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

class InsideSecondCycleStrategy extends PracticeWordSetInteractorStrategy {
    private final WordRepetitionProgressService progressService;
    private final CurrentPracticeStateService currentPracticeStateService;

    InsideSecondCycleStrategy(PracticeWordSetInteractor interactor, WordRepetitionProgressService progressService, CurrentPracticeStateService currentPracticeStateService) {
        super(interactor);
        this.progressService = progressService;
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    void initialiseExperience(OnPracticeWordSetListener listener) {
        listener.onEnableRepetitionMode();
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        progressService.shiftSentences(currentPracticeStateService.getCurrentWord());
        listener.onRightAnswer(currentPracticeStateService.getCurrentSentence());
    }
}