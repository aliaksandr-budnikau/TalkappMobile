package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

class InsideSecondCycleStrategy extends PracticeWordSetInteractorStrategy {
    private final WordRepetitionProgressService progressService;

    InsideSecondCycleStrategy(PracticeWordSetInteractor interactor, WordRepetitionProgressService progressService) {
        super(interactor);
        this.progressService = progressService;
    }

    @Override
    void initialiseExperience(OnPracticeWordSetListener listener) {
        listener.onEnableRepetitionMode();
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        progressService.shiftSentences(getInteractor().getCurrentWord());
        listener.onRightAnswer(getInteractor().getCurrentSentence());
    }
}