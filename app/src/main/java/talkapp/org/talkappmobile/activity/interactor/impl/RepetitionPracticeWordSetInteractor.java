package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static java.util.Collections.singletonList;

public class RepetitionPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = RepetitionPracticeWordSetInteractor.class.getSimpleName();
    private final SentenceService sentenceService;
    private final Logger logger;
    private final WordRepetitionProgressService exerciseService;
    private final WordSetExperienceUtils experienceUtils;
    private final WordTranslationService wordTranslationService;
    private final CurrentPracticeStateService currentPracticeStateService;
    private int maxTrainingProgress;

    public RepetitionPracticeWordSetInteractor(
            SentenceService sentenceService,
            RefereeService refereeService,
            Logger logger,
            WordRepetitionProgressService exerciseService,
            WordSetExperienceUtils experienceUtils,
            WordTranslationService wordTranslationService,
            Context context,
            CurrentPracticeStateService currentPracticeStateService,
            AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceService, audioStuffFactory, currentPracticeStateService);
        this.sentenceService = sentenceService;
        this.logger = logger;
        this.exerciseService = exerciseService;
        this.experienceUtils = experienceUtils;
        this.wordTranslationService = wordTranslationService;
        this.currentPracticeStateService = currentPracticeStateService;
    }

    @Override
    public void initialiseExperience(OnPracticeWordSetListener listener) {
        WordSet wordSet = currentPracticeStateService.getWordSet();
        maxTrainingProgress = experienceUtils.getMaxTrainingProgress(wordSet) / 2;
        logger.i(TAG, "enable repetition mode");
        listener.onEnableRepetitionMode();
        currentPracticeStateService.setTrainingExperience(0);
        wordSet = currentPracticeStateService.getWordSet();
        listener.onInitialiseExperience(wordSet);
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId() {
        List<Word2Tokens> allWords = currentPracticeStateService.getAllWords();
        return peekRandomWordWithoutCurrentWord(allWords, currentPracticeStateService.getCurrentWord());
    }

    @Override
    public void initialiseSentence(Word2Tokens word, OnPracticeWordSetListener listener) {
        currentPracticeStateService.setCurrentWord(word);
        List<Sentence> sentences = sentenceService.fetchSentencesNotFromServerByWordAndWordSetId(word);
        if (sentences.isEmpty()) {
            WordTranslation wordTranslation = wordTranslationService.findByWordAndLanguage(word.getWord(), "russian");
            if (wordTranslation == null) {
                return;
            }
            sentences = singletonList(sentenceService.convertToSentence(wordTranslation));
        }
        setCurrentSentence(sentences.get(0));
        listener.onSentencesFound(getCurrentSentence(), word);
    }

    @Override
    public boolean checkAnswer(String answer, OnPracticeWordSetListener listener) {
        Sentence sentence = currentPracticeStateService.getCurrentSentence();
        Word2Tokens currentWord = currentPracticeStateService.getCurrentWord();
        if (!super.checkAccuracyOfAnswer(answer, currentWord, sentence, listener)) {
            return false;
        }

        if (isAnswerHasBeenSeen()) {
            int counter = exerciseService.markAsForgottenAgain(currentWord);
            listener.onForgottenAgain(counter);
            listener.onRightAnswer(sentence);
            return false;
        }

        WordSet wordSet = currentPracticeStateService.getWordSet();
        currentPracticeStateService.setTrainingExperience(wordSet.getTrainingExperience() + 1);
        currentPracticeStateService.addFinishedWord(currentPracticeStateService.getCurrentWord());
        wordSet = currentPracticeStateService.getWordSet();
        listener.onUpdateProgress(wordSet.getTrainingExperience(), maxTrainingProgress);
        exerciseService.markAsRepeated(currentWord);
        exerciseService.shiftSentences(currentWord);
        return true;
    }

    @Override
    protected Word2Tokens peekRandomWordWithoutCurrentWord(List<Word2Tokens> words, Word2Tokens currentWord) {
        LinkedList<Word2Tokens> leftOver = new LinkedList<>(words);
        for (Word2Tokens word2Tokens : currentPracticeStateService.getFinishedWords()) {
            leftOver.remove(word2Tokens);
        }
        return super.peekRandomWordWithoutCurrentWord(leftOver, currentWord);
    }
}