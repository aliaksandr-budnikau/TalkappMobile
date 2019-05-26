package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;

import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.database.UserExpService;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.component.database.WordSetService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

public class StudyingPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = StudyingPracticeWordSetInteractor.class.getSimpleName();
    private final WordsCombinator wordsCombinator;
    private final SentenceProvider sentenceProvider;
    private final SentenceSelector sentenceSelector;
    private final Logger logger;
    private final WordSetService experienceService;
    private final WordSetExperienceUtils experienceUtils;
    private final WordRepetitionProgressService exerciseService;
    private final UserExpService userExpService;
    private Word2Tokens currentWord;

    public StudyingPracticeWordSetInteractor(WordsCombinator wordsCombinator,
                                             SentenceProvider sentenceProvider,
                                             SentenceSelector sentenceSelector,
                                             RefereeService refereeService,
                                             Logger logger,
                                             WordSetService experienceService,
                                             WordRepetitionProgressService exerciseService,
                                             UserExpService userExpService,
                                             WordSetExperienceUtils experienceUtils,
                                             Context context,
                                             AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceProvider, audioStuffFactory);
        this.wordsCombinator = wordsCombinator;
        this.sentenceProvider = sentenceProvider;
        this.sentenceSelector = sentenceSelector;
        this.logger = logger;
        this.experienceService = experienceService;
        this.exerciseService = exerciseService;
        this.userExpService = userExpService;
        this.experienceUtils = experienceUtils;
    }

    @Override
    public void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener) {
        if (wordSet.getTrainingExperience() == 0) {
            logger.i(TAG, "create new experience");
            experienceService.resetProgress(wordSet);
            wordSet.setTrainingExperience(0);
            wordSet.setStatus(FIRST_CYCLE);
        }
        if (SECOND_CYCLE.equals(wordSet.getStatus())) {
            logger.i(TAG, "enable repetition mode");
            sentenceProvider.enableRepetitionMode();
            listener.onEnableRepetitionMode();
        } else {
            logger.i(TAG, "disable repetition mode for state {} ", wordSet.getStatus());
            sentenceProvider.disableRepetitionMode();
        }
        logger.i(TAG, "experience was initialized");
        listener.onInitialiseExperience(wordSet);
    }

    @Override
    public void initialiseWordsSequence(WordSet wordSet, OnPracticeWordSetListener listener) {
        logger.i(TAG, "initialise words sequence {}", wordSet);
        Set<Word2Tokens> words = wordsCombinator.combineWords(wordSet.getWords());
        logger.i(TAG, "words sequence {}", words);
        exerciseService.createSomeIfNecessary(words, wordSet.getId());
        logger.i(TAG, "word sequence was initialized");
    }

    @Override
    public void initialiseSentence(Word2Tokens word, int wordSetId, final OnPracticeWordSetListener listener) {
        this.currentWord = word;
        logger.i(TAG, "initialise sentence for word {}, for word set id {}", word, wordSetId);
        List<Sentence> sentences = sentenceProvider.findByWordAndWordSetId(word, wordSetId);
        logger.i(TAG, "sentences size {}", sentences.size());
        if (sentences.isEmpty()) {
            logger.w(TAG, "Sentences haven't been found with words '{}'. Fill the storage.", word);
            return;
        }
        sentenceSelector.orderByScore(sentences);
        final Sentence sentence = sentenceSelector.selectSentence(sentences);
        replaceSentence(sentence, word, wordSetId, listener);
    }

    @Override
    protected void replaceSentence(Sentence sentence, Word2Tokens word, int wordSetId, final OnPracticeWordSetListener listener) {
        exerciseService.save(word, wordSetId, sentence);
        listener.onSentencesFound(sentence, word);
    }

    @Override
    public boolean checkAnswer(String answer, final WordSet wordSet, final Sentence sentence, boolean answerHasBeenSeen, final OnPracticeWordSetListener listener) {
        if (!super.checkAccuracyOfAnswer(answer, currentWord, sentence, listener)) {
            return false;
        }

        if (answerHasBeenSeen) {
            listener.onRightAnswer(sentence);
            return false;
        }

        int experience = experienceService.increaseExperience(wordSet, 1);
        wordSet.setTrainingExperience(experience);
        listener.onUpdateProgress(wordSet);

        exerciseService.moveCurrentWordToNextState(wordSet.getId());
        double expScore = userExpService.increaseForRepetition(1, WORD_SET_PRACTICE);
        listener.onUpdateUserExp(expScore);
        if (wordSet.getTrainingExperience() == experienceUtils.getMaxTrainingProgress(wordSet) / 2) {
            logger.i(TAG, "training half finished");
            experienceService.moveToAnotherState(wordSet.getId(), SECOND_CYCLE);
            wordSet.setStatus(SECOND_CYCLE);
            sentenceProvider.enableRepetitionMode();
            listener.onTrainingHalfFinished(sentence);
            listener.onEnableRepetitionMode();
        } else if (wordSet.getTrainingExperience() == experienceUtils.getMaxTrainingProgress(wordSet)) {
            logger.i(TAG, "training finished");
            experienceService.moveToAnotherState(wordSet.getId(), FINISHED);
            wordSet.setStatus(FINISHED);
            listener.onTrainingFinished();
        } else {
            logger.i(TAG, "right answer");
            listener.onRightAnswer(sentence);
        }
        return true;
    }

    @Override
    public Sentence getCurrentSentence(int wordSetId) {
        return exerciseService.getCurrentSentence(wordSetId);
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId(int wordSetId) {
        Word2Tokens currentWord = exerciseService.getCurrentWord(wordSetId);
        exerciseService.putOffCurrentWord(wordSetId);
        List<Word2Tokens> leftOver = exerciseService.getLeftOverOfWordSetByWordSetId(wordSetId);
        Word2Tokens newCurrentWord = peekRandomWordWithoutCurrentWord(leftOver, currentWord);
        exerciseService.markNewCurrentWordByWordSetIdAndWord(wordSetId, newCurrentWord);
        return newCurrentWord;
    }
}