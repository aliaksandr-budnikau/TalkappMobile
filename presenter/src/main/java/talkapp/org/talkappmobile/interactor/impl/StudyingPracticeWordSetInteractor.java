package talkapp.org.talkappmobile.interactor.impl;

import android.content.Context;

import java.util.List;

import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordTranslationService;

public class StudyingPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private final WordRepetitionProgressService exerciseService;
    private final CurrentPracticeStateService currentPracticeStateService;

    public StudyingPracticeWordSetInteractor(SentenceService sentenceService,
                                             RefereeService refereeService,
                                             Logger logger,
                                             WordTranslationService wordTranslationService,
                                             CurrentPracticeStateService currentPracticeStateService,
                                             WordRepetitionProgressService exerciseService,
                                             Context context,
                                             SentenceProvider sentenceProvider,
                                             AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceService, audioStuffFactory, currentPracticeStateService, sentenceProvider);
        this.currentPracticeStateService = currentPracticeStateService;
        this.exerciseService = exerciseService;
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId() {
        WordSet wordSet = currentPracticeStateService.getWordSet();
        List<Word2Tokens> leftOver = exerciseService.getLeftOverOfWordSetByWordSetId(wordSet.getId());
        return peekRandomWordWithoutCurrentWord(leftOver, currentPracticeStateService.getCurrentWord());
    }

    @Override
    public boolean checkAnswer(String answer, final OnPracticeWordSetListener listener) {
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
        currentPracticeStateService.persistWordSet();
        wordSet = currentPracticeStateService.getWordSet();
        listener.onUpdateProgress(wordSet);
        exerciseService.moveCurrentWordToNextState(currentWord);
        return true;
    }
}