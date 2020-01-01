package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.WordSetService;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

class FirstCycleFinishedStrategy extends PracticeWordSetInteractorStrategy {
    private final WordSetService wordSetService;
    private final CurrentPracticeStateService currentPracticeStateService;

    FirstCycleFinishedStrategy(PracticeWordSetInteractor interactor, WordSetService wordSetService, CurrentPracticeStateService currentPracticeStateService) {
        super(interactor);
        this.wordSetService = wordSetService;
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        CurrentPracticeState currentPracticeState = currentPracticeStateService.get();
        wordSetService.moveToAnotherState(currentPracticeState.getWordSet().getId(), SECOND_CYCLE);
        currentPracticeState.getWordSet().setStatus(SECOND_CYCLE);
        currentPracticeStateService.save(currentPracticeState);
        listener.onTrainingHalfFinished(currentPracticeState.getCurrentSentence());
        listener.onEnableRepetitionMode();
    }
}