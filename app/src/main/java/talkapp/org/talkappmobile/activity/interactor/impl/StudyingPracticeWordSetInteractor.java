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
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.UserExpService;
import talkapp.org.talkappmobile.component.database.WordSetService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.SECOND_CYCLE;

public class StudyingPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = StudyingPracticeWordSetInteractor.class.getSimpleName();
    private final WordsCombinator wordsCombinator;
    private final SentenceProvider sentenceProvider;
    private final SentenceSelector sentenceSelector;
    private final Logger logger;
    private final WordSetService experienceService;
    private final PracticeWordSetExerciseService exerciseService;
    private final UserExpService userExpService;

    public StudyingPracticeWordSetInteractor(WordsCombinator wordsCombinator,
                                             SentenceProvider sentenceProvider,
                                             SentenceSelector sentenceSelector,
                                             RefereeService refereeService,
                                             Logger logger,
                                             WordSetService experienceService,
                                             PracticeWordSetExerciseService exerciseService,
                                             UserExpService userExpService,
                                             Context context,
                                             AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, audioStuffFactory);
        this.wordsCombinator = wordsCombinator;
        this.sentenceProvider = sentenceProvider;
        this.sentenceSelector = sentenceSelector;
        this.logger = logger;
        this.experienceService = experienceService;
        this.exerciseService = exerciseService;
        this.userExpService = userExpService;
    }

    @Override
    public void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener) {
        WordSetExperience exp = experienceService.findById(wordSet.getId());
        logger.i(TAG, "find experience by id {}, word set {}", exp, wordSet);
        if (exp.getTrainingExperience() == 0) {
            logger.i(TAG, "create new experience");
            exp = experienceService.createNew(wordSet);
        }
        if (SECOND_CYCLE.equals(exp.getStatus())) {
            logger.i(TAG, "enable repetition mode");
            sentenceProvider.enableRepetitionMode();
            listener.onEnableRepetitionMode();
        } else {
            logger.i(TAG, "disable repetition mode for state {} ", exp.getStatus());
            sentenceProvider.disableRepetitionMode();
        }
        logger.i(TAG, "experience was initialized");
        listener.onInitialiseExperience(exp);
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
        logger.i(TAG, "initialise sentence for word {}, for word set id {}", word, wordSetId);
        List<Sentence> sentences = sentenceProvider.findByWordAndWordSetId(word, wordSetId);
        logger.i(TAG, "sentences size {}", sentences.size());
        if (sentences.isEmpty()) {
            logger.w(TAG, "Sentences haven't been found with words '{}'. Fill the storage.", word);
            return;
        }
        sentenceSelector.orderByScore(sentences);
        final Sentence sentence = sentenceSelector.selectSentence(sentences);
        logger.i(TAG, "chosen sentence {}", sentences);
        exerciseService.save(word, wordSetId, sentence);
        listener.onSentencesFound(sentence, word);
        logger.i(TAG, "sentence was initialized");
    }

    @Override
    public boolean checkAnswer(String answer, final WordSet wordSet, final Sentence sentence, boolean answerHasBeenSeen, final OnPracticeWordSetListener listener) {
        if (!super.checkAnswer(answer, wordSet, sentence, answerHasBeenSeen, listener)) {
            return false;
        }

        if (answerHasBeenSeen) {
            listener.onRightAnswer(sentence);
            return false;
        }

        WordSetExperience exp = experienceService.increaseExperience(wordSet.getId(), 1);
        logger.i(TAG, "experience is {}", exp);
        listener.onUpdateProgress(exp);

        exerciseService.moveCurrentWordToNextState(wordSet.getId());
        double expScore = userExpService.increaseForRepetition(0, WORD_SET_PRACTICE);
        listener.onUpdateUserExp(expScore);
        if (exp.getTrainingExperience() == exp.getMaxTrainingExperience() / 2) {
            logger.i(TAG, "training half finished");
            experienceService.moveToAnotherState(wordSet.getId(), SECOND_CYCLE);
            sentenceProvider.enableRepetitionMode();
            listener.onTrainingHalfFinished(sentence);
            listener.onEnableRepetitionMode();
        } else if (exp.getTrainingExperience() == exp.getMaxTrainingExperience()) {
            logger.i(TAG, "training finished");
            experienceService.moveToAnotherState(wordSet.getId(), FINISHED);
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
        exerciseService.putOffCurrentWord(wordSetId);
        return exerciseService.peekByWordSetIdAnyWord(wordSetId);
    }
}