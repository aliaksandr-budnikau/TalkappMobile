package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordsCombinator;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;

import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

public class StudyingPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = StudyingPracticeWordSetInteractor.class.getSimpleName();
    private final SentenceService sentenceService;
    private final Logger logger;
    private final WordSetService experienceService;
    private final WordSetExperienceUtils experienceUtils;
    private final WordRepetitionProgressService exerciseService;
    private final UserExpService userExpService;
    private Word2Tokens currentWord;
    private Sentence currentSentence;

    public StudyingPracticeWordSetInteractor(WordsCombinator wordsCombinator,
                                             SentenceService sentenceService,
                                             RefereeService refereeService,
                                             Logger logger,
                                             WordSetService experienceService,
                                             WordRepetitionProgressService exerciseService,
                                             UserExpService userExpService,
                                             WordSetExperienceUtils experienceUtils,
                                             Context context,
                                             AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceService, wordsCombinator, audioStuffFactory);
        this.sentenceService = sentenceService;
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
            listener.onEnableRepetitionMode();
        } else {
            logger.i(TAG, "disable repetition mode for state {} ", wordSet.getStatus());
        }
        logger.i(TAG, "experience was initialized");
        listener.onInitialiseExperience(wordSet);
    }

    @Override
    public void initialiseSentence(Word2Tokens word, final OnPracticeWordSetListener listener) {
        this.currentWord = word;
        List<Sentence> sentences = exerciseService.findByWordAndWordSetId(word);
        if (sentences.isEmpty()) {
            sentences = sentenceService.fetchSentencesFromServerByWordAndWordSetId(word);
            if (sentences.isEmpty()) {
                return;
            }
            sentenceService.orderByScore(sentences);
            List<Sentence> selectSentences = sentenceService.selectSentences(sentences);
            replaceSentence(selectSentences, word, listener);
        } else {
            setCurrentSentence(sentences.get(0));
            listener.onSentencesFound(getCurrentSentence(), word);
        }
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
            listener.onTrainingHalfFinished(sentence);
            listener.onEnableRepetitionMode();
        } else if (wordSet.getTrainingExperience() == experienceUtils.getMaxTrainingProgress(wordSet)) {
            logger.i(TAG, "training finished");
            experienceService.moveToAnotherState(wordSet.getId(), FINISHED);
            exerciseService.shiftSentences(currentWord);
            wordSet.setStatus(FINISHED);
            listener.onTrainingFinished();
        } else {
            if (wordSet.getStatus() == SECOND_CYCLE) {
                exerciseService.shiftSentences(currentWord);
            }
            listener.onRightAnswer(sentence);
        }
        return true;
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


    @Override
    public Sentence getCurrentSentence() {
        return currentSentence;
    }

    @Override
    protected void setCurrentSentence(Sentence sentence) {
        this.currentSentence = sentence;
    }

    @Override
    protected Word2Tokens getCurrentWord() {
        return currentWord;
    }
}