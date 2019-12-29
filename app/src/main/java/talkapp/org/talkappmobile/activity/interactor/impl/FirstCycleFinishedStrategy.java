package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.PracticeState;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordSetService;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

class FirstCycleFinishedStrategy extends PracticeWordSetInteractorStrategy {
    private final WordSetService wordSetService;

    FirstCycleFinishedStrategy(PracticeWordSetInteractor interactor, WordSetService wordSetService) {
        super(interactor);
        this.wordSetService = wordSetService;
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        PracticeState practiceState = wordSetService.getCurrent();
        wordSetService.moveToAnotherState(practiceState.getWordSet().getId(), SECOND_CYCLE);
        practiceState.getWordSet().setStatus(SECOND_CYCLE);
        wordSetService.saveCurrent(practiceState);
        listener.onTrainingHalfFinished(getInteractor().getCurrentSentence());
        listener.onEnableRepetitionMode();
    }
}