package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.model.Word2Tokens;
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
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        wordSetService.moveToAnotherState(currentPracticeState.getWordSet().getId(), FINISHED);
        progressService.shiftSentences(getCurrentWord());
        currentPracticeState.getWordSet().setStatus(FINISHED);
        listener.onTrainingFinished();
    }

    private Word2Tokens getCurrentWord() {
        CurrentPracticeState currentPracticeState = wordSetService.getCurrentPracticeState();
        CurrentPracticeState.WordSource currentWord = currentPracticeState.getCurrentWord();
        if (currentWord == null) {
            return null;
        }
        return wordSetService.findById(currentWord.getWordSetId()).getWords().get(currentWord.getWordIndex());
    }
}