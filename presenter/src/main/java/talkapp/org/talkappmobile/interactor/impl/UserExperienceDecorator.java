package talkapp.org.talkappmobile.interactor.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.ExpActivityType;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

@RequiredArgsConstructor
public class UserExperienceDecorator implements PracticeWordSetInteractor {
    @Delegate(excludes = ExcludedMethods.class)
    private final PracticeWordSetInteractor interactor;
    private final UserExpService userExpService;
    private final CurrentPracticeStateService currentPracticeStateService;
    private final WordRepetitionProgressService progressService;

    @Override
    public boolean checkAnswer(String answer, OnPracticeWordSetListener listener) {
        boolean result = interactor.checkAnswer(answer, listener);
        if (!result) {
            return false;
        }
        WordSet wordSet = currentPracticeStateService.getWordSet();
        double expScore;
        if (wordSet.getStatus() == WordSetProgressStatus.FINISHED || wordSet.getId() == 0) {
            Word2Tokens word = currentPracticeStateService.getCurrentWord();
            int repetitionCounter = progressService.getRepetitionCounter(word);
            expScore = userExpService.increaseForRepetition(repetitionCounter, ExpActivityType.WORD_SET_PRACTICE);
        } else {
            expScore = userExpService.increaseForRepetition(1, ExpActivityType.WORD_SET_PRACTICE);
        }
        listener.onUpdateUserExp(expScore);
        return true;
    }

    private interface ExcludedMethods {
        boolean checkAnswer(String answer, OnPracticeWordSetListener listener);
    }
}