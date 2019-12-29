package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;

class InsideSecondCycleStrategy extends PracticeWordSetInteractorStrategy {
    private final WordSetService wordSetService;
    private final WordRepetitionProgressService progressService;

    InsideSecondCycleStrategy(PracticeWordSetInteractor interactor, WordSetService wordSetService, WordRepetitionProgressService progressService) {
        super(interactor);
        this.wordSetService = wordSetService;
        this.progressService = progressService;
    }

    @Override
    void initialiseExperience(OnPracticeWordSetListener listener) {
        listener.onEnableRepetitionMode();
    }

    @Override
    void finishWord(OnPracticeWordSetListener listener) {
        Word2Tokens word2Tokens = getCurrentWord();
        progressService.shiftSentences(word2Tokens);
        listener.onRightAnswer(getInteractor().getCurrentSentence());
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