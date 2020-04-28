package talkapp.org.talkappmobile.interactor.impl;

import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

public class RepetitionPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private final WordRepetitionProgressService exerciseService;
    private final CurrentPracticeStateService currentPracticeStateService;

    public RepetitionPracticeWordSetInteractor(
            SentenceService sentenceService,
            RefereeService refereeService,
            Logger logger,
            WordRepetitionProgressService exerciseService,
            SentenceProvider sentenceProvider,
            CurrentPracticeStateService currentPracticeStateService) {
        super(logger, refereeService, exerciseService, sentenceService, currentPracticeStateService, sentenceProvider);
        this.exerciseService = exerciseService;
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    public boolean checkAnswer(String answer, OnPracticeWordSetListener listener) {
        Sentence sentence = currentPracticeStateService.getCurrentSentence();
        Word2Tokens currentWord = currentPracticeStateService.getCurrentWord();
        if (!super.checkAccuracyOfAnswer(answer, currentWord, sentence, listener)) {
            return false;
        }

        if (isAnswerHasBeenSeen()) {
            exerciseService.markAsForgottenAgain(currentWord);
            listener.onRightAnswer(sentence);
            return false;
        }

        WordSet wordSet = currentPracticeStateService.getWordSet();
        currentPracticeStateService.setTrainingExperience(wordSet.getTrainingExperience() + 1);
        wordSet = currentPracticeStateService.getWordSet();
        listener.onUpdateProgress(wordSet);
        exerciseService.markAsRepeated(currentWord);
        exerciseService.shiftSentences(currentWord);
        return true;
    }
}