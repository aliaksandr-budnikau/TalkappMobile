package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.PracticeState;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;

import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;

class SecondCycleFinishedStrategy extends PracticeWordSetInteractorStrategy {
    private final WordRepetitionProgressService progressService;
    private final WordSetService wordSetService;

    SecondCycleFinishedStrategy(PracticeWordSetInteractor interactor, WordSetService wordSetService, WordRepetitionProgressService progressService) {
        super(interactor);
        this.wordSetService = wordSetService;
        this.progressService = progressService;
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        PracticeState practiceState = wordSetService.getCurrent();
        wordSetService.moveToAnotherState(practiceState.getWordSet().getId(), FINISHED);
        progressService.shiftSentences(getInteractor().getCurrentWord());
        practiceState.getWordSet().setStatus(FINISHED);
        listener.onTrainingFinished();
    }
}