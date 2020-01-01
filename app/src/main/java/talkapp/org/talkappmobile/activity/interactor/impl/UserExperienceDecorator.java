package talkapp.org.talkappmobile.activity.interactor.impl;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;

import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;

public class UserExperienceDecorator extends PracticeWordSetInteractorDecorator {

    private final WordSetService wordSetService;
    private final UserExpService userExpService;
    private final CurrentPracticeStateService currentPracticeStateService;
    private final WordRepetitionProgressService progressService;

    public UserExperienceDecorator(PracticeWordSetInteractor interactor,
                                   WordSetService wordSetService,
                                   UserExpService userExpService,
                                   CurrentPracticeStateService currentPracticeStateService,
                                   WordRepetitionProgressService progressService) {
        super(interactor);
        this.wordSetService = wordSetService;
        this.userExpService = userExpService;
        this.currentPracticeStateService = currentPracticeStateService;
        this.progressService = progressService;
    }

    @Override
    public boolean checkAnswer(String answer, OnPracticeWordSetListener listener) {
        boolean result = super.checkAnswer(answer, listener);
        if (!result) {
            return false;
        }
        CurrentPracticeState currentPracticeState = currentPracticeStateService.get();
        WordSet wordSet = currentPracticeState.getWordSet();
        double expScore;
        if (wordSet.getStatus() == FINISHED || wordSet.getId() == 0) {
            CurrentPracticeState.WordSource currentWord = currentPracticeState.getCurrentWord();
            WordSet wordSetSource = wordSetService.findById(currentWord.getWordSetId());
            int repetitionCounter = progressService.getRepetitionCounter(wordSetSource.getWords().get(currentWord.getWordIndex()));
            expScore = userExpService.increaseForRepetition(repetitionCounter, WORD_SET_PRACTICE);
        } else {
            expScore = userExpService.increaseForRepetition(1, WORD_SET_PRACTICE);
        }
        listener.onUpdateUserExp(expScore);
        return true;
    }
}