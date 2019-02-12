package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;

import java.util.List;
import java.util.Random;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.UserExpService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class RepetitionPracticeWordSetInteractor extends AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = RepetitionPracticeWordSetInteractor.class.getSimpleName();
    private final SentenceProvider sentenceProvider;
    private final Logger logger;
    private final SentenceSelector sentenceSelector;
    private final PracticeWordSetExerciseService exerciseService;
    private final UserExpService userExpService;
    private WordSetExperience exp;
    private Word2Tokens currentWord;
    private Sentence currentSentence;
    private WordSet wordSet;

    public RepetitionPracticeWordSetInteractor(
            SentenceProvider sentenceProvider,
            SentenceSelector sentenceSelector,
            RefereeService refereeService,
            Logger logger,
            PracticeWordSetExerciseService exerciseService,
            UserExpService userExpService,
            Context context,
            AudioStuffFactory audioStuffFactory) {
        super(logger, context, refereeService, exerciseService, audioStuffFactory);
        this.sentenceProvider = sentenceProvider;
        this.sentenceSelector = sentenceSelector;
        this.logger = logger;
        this.exerciseService = exerciseService;
        this.userExpService = userExpService;
    }

    @Override
    public Sentence getCurrentSentence(int wordSetId) {
        return currentSentence;
    }

    @Override
    public void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener) {
        this.wordSet = wordSet;
        logger.i(TAG, "enable repetition mode");
        sentenceProvider.enableRepetitionMode();
        listener.onEnableRepetitionMode();
        exp = new WordSetExperience();
        exp.setTrainingExperience(0);
        exp.setMaxTrainingExperience(wordSet.getWords().size());
        listener.onInitialiseExperience(exp);
    }

    @Override
    public void initialiseWordsSequence(WordSet wordSet, OnPracticeWordSetListener listener) {
        // do nothing
    }

    @Override
    public Word2Tokens peekAnyNewWordByWordSetId(int wordSetId) {
        List<Word2Tokens> words = wordSet.getWords();
        int i = new Random().nextInt(words.size());
        return words.get(i);
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
    public boolean checkAnswer(String answer, WordSet wordSet, Sentence sentence, boolean answerHasBeenSeen, OnPracticeWordSetListener listener) {
        if (!super.checkAnswer(answer, wordSet, sentence, answerHasBeenSeen, listener)) {
            return false;
        }

        if (answerHasBeenSeen) {
            listener.onRightAnswer(sentence);
            return false;
        }

        exp.setTrainingExperience(exp.getTrainingExperience() + 1);
        wordSet.getWords().remove(currentWord);
        logger.i(TAG, "experience is {}", exp);
        listener.onUpdateProgress(exp);
        int repetitionCounter = exerciseService.markAsRepeated(currentWord, sentence);
        userExpService.increaseForRepetition(repetitionCounter);
        if (exp.getTrainingExperience() == exp.getMaxTrainingExperience()) {
            logger.i(TAG, "training finished");
            listener.onTrainingFinished();
        } else {
            logger.i(TAG, "right answer");
            listener.onRightAnswer(sentence);
        }
        return true;
    }
}