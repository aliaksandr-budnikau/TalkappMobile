package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.service.WordSetService;

class InsideRepetitionCycleStrategy extends PracticeWordSetInteractorStrategy {
    private final WordSetService wordSetService;

    public InsideRepetitionCycleStrategy(PracticeWordSetInteractor interactor, WordSetService wordSetService) {
        super(interactor);
        this.wordSetService = wordSetService;
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        listener.onRightAnswer(currentPracticeState.getCurrentSentence());
    }
}