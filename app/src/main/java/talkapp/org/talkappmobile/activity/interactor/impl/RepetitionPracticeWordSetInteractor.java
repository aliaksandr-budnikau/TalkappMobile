package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
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
import talkapp.org.talkappmobile.service.WordsCombinator;

import static talkapp.org.talkappmobile.model.ExpActivityType.WORD_SET_PRACTICE;

public class RepetitionPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = RepetitionPracticeWordSetInteractor.class.getSimpleName();
    private final SentenceService sentenceService;
    private final Logger logger;
    private final WordRepetitionProgressService exerciseService;
    private final UserExpService userExpService;
    private final WordSetExperienceUtils experienceUtils;
    private Word2Tokens currentWord;
    private Sentence currentSentence;
    private int maxTrainingProgress;
    private List<Word2Tokens> finishedWords = new LinkedList<>();
    private WordSet wordSet;

    public RepetitionPracticeWordSetInteractor(
            SentenceService sentenceService,
            RefereeService refereeService,
            Logger logger,
            WordRepetitionProgressService exerciseService,
            UserExpService userExpService,
            WordSetExperienceUtils experienceUtils,
            WordsCombinator wordsCombinator,
            Context context,
            AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, sentenceService, wordsCombinator, audioStuffFactory);
        this.sentenceService = sentenceService;
        this.logger = logger;
        this.exerciseService = exerciseService;
        this.userExpService = userExpService;
        this.experienceUtils = experienceUtils;
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
    public void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener) {
        this.wordSet = wordSet;
        maxTrainingProgress = experienceUtils.getMaxTrainingProgress(wordSet) / 2;
        logger.i(TAG, "enable repetition mode");
        listener.onEnableRepetitionMode();
        wordSet.setTrainingExperience(0);
        listener.onInitialiseExperience(wordSet);
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId(int wordSetId) {
        return peekRandomWordWithoutCurrentWord(wordSet.getWords(), currentWord);
    }

    @Override
    public void initialiseSentence(Word2Tokens word, OnPracticeWordSetListener listener) {
        this.currentWord = word;
        List<Sentence> sentences = sentenceService.fetchSentencesNotFromServerByWordAndWordSetId(word);
        if (sentences.isEmpty()) {
            return;
        }
        setCurrentSentence(sentences.get(0));
        listener.onSentencesFound(getCurrentSentence(), word);
    }

    @Override
    public boolean checkAnswer(String answer, WordSet wordSet, Sentence sentence, boolean answerHasBeenSeen, OnPracticeWordSetListener listener) {
        if (!super.checkAccuracyOfAnswer(answer, currentWord, sentence, listener)) {
            return false;
        }

        if (answerHasBeenSeen) {
            int counter = exerciseService.markAsForgottenAgain(currentWord);
            listener.onForgottenAgain(counter);
            listener.onRightAnswer(sentence);
            return false;
        }

        wordSet.setTrainingExperience(wordSet.getTrainingExperience() + 1);
        finishedWords.add(currentWord);
        listener.onUpdateProgress(wordSet, maxTrainingProgress);
        int repetitionCounter = exerciseService.markAsRepeated(currentWord);
        exerciseService.shiftSentences(currentWord);
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

    @Override
    protected Word2Tokens peekRandomWordWithoutCurrentWord(List<Word2Tokens> words, Word2Tokens currentWord) {
        LinkedList<Word2Tokens> leftOver = new LinkedList<>(words);
        leftOver.removeAll(finishedWords);
        return super.peekRandomWordWithoutCurrentWord(leftOver, currentWord);
    }

    @Override
    protected Word2Tokens getCurrentWord() {
        return currentWord;
    }
}