package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.database.UserExpService;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.component.database.WordSetService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

public class RepetitionPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = RepetitionPracticeWordSetInteractor.class.getSimpleName();
    private final SentenceProvider sentenceProvider;
    private final Logger logger;
    private final SentenceSelector sentenceSelector;
    private final WordRepetitionProgressService exerciseService;
    private final UserExpService userExpService;
    private final WordSetService wordSetService;
    private final WordSetExperienceUtils experienceUtils;
    private Word2Tokens currentWord;
    private Sentence currentSentence;
    private WordSet wordSet;
    private int maxTrainingProgress;

    public RepetitionPracticeWordSetInteractor(
            SentenceProvider sentenceProvider,
            SentenceSelector sentenceSelector,
            RefereeService refereeService,
            Logger logger,
            WordRepetitionProgressService exerciseService,
            UserExpService userExpService,
            WordSetService wordSetService,
            WordSetExperienceUtils experienceUtils,
            Context context,
            AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceProvider, audioStuffFactory);
        this.sentenceProvider = sentenceProvider;
        this.sentenceSelector = sentenceSelector;
        this.logger = logger;
        this.exerciseService = exerciseService;
        this.wordSetService = wordSetService;
        this.userExpService = userExpService;
        this.experienceUtils = experienceUtils;
    }

    @Override
    public Sentence getCurrentSentence(int wordSetId) {
        return currentSentence;
    }

    @Override
    public void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener) {
        this.wordSet = wordSet;
        maxTrainingProgress = experienceUtils.getMaxTrainingProgress(wordSet) / 2;
        logger.i(TAG, "enable repetition mode");
        sentenceProvider.enableRepetitionMode();
        listener.onEnableRepetitionMode();
        wordSet.setTrainingExperience(0);
        listener.onInitialiseExperience(wordSet);
    }

    @Override
    public void initialiseWordsSequence(WordSet wordSet, OnPracticeWordSetListener listener) {
        // do nothing
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId(int wordSetId) {
        return peekRandomWordWithoutCurrentWord(wordSet.getWords(), currentWord);
    }

    @Override
    public void initialiseSentence(Word2Tokens word, int wordSetId, OnPracticeWordSetListener listener) {
        this.currentWord = word;
        logger.i(TAG, "initialise currentSentence for currentWord {}, for currentWord set id {}", word, wordSetId);
        List<Sentence> sentences = sentenceProvider.findByWordAndWordSetId(word, wordSetId);
        logger.i(TAG, "sentences size {}", sentences.size());
        if (sentences.isEmpty()) {
            logger.w(TAG, "Sentences haven't been found with words '{}'. Check the db.", word);
            return;
        }
        currentSentence = sentenceSelector.selectSentence(sentences);
        logger.i(TAG, "chosen currentSentence {}", currentSentence);
        listener.onSentencesFound(currentSentence, word);
        logger.i(TAG, "currentSentence was initialized");
    }

    @Override
    protected void replaceSentence(Sentence sentence, Word2Tokens word, int wordSetId, OnPracticeWordSetListener listener) {
        // do nothing
    }

    @Override
    public boolean checkAnswer(String answer, WordSet wordSet, Sentence sentence, boolean answerHasBeenSeen, OnPracticeWordSetListener listener) {
        if (!super.checkAccuracyOfAnswer(answer, currentWord, sentence, listener)) {
            return false;
        }

        if (answerHasBeenSeen) {
            listener.onRightAnswer(sentence);
            return false;
        }

        wordSet.setTrainingExperience(wordSet.getTrainingExperience() + 1);
        wordSet.getWords().remove(currentWord);
        listener.onUpdateProgress(wordSet, maxTrainingProgress);
        int repetitionCounter = exerciseService.markAsRepeated(currentWord, sentence);
        double expScore = userExpService.increaseForRepetition(repetitionCounter, WORD_SET_PRACTICE);
        listener.onUpdateUserExp(expScore);
        if (wordSet.getTrainingExperience() == maxTrainingProgress) {
            logger.i(TAG, "training finished");
            listener.onTrainingFinished();
        } else {
            logger.i(TAG, "right answer");
            listener.onRightAnswer(sentence);
        }
        return true;
    }
}