package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.REPETITION;

public class StudyingPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = StudyingPracticeWordSetInteractor.class.getSimpleName();
    private final WordsCombinator wordsCombinator;
    private final SentenceProvider sentenceProvider;
    private final SentenceSelector sentenceSelector;
    private final RefereeService refereeService;
    private final Logger logger;
    private final WordSetExperienceService experienceService;
    private final PracticeWordSetExerciseService exerciseService;
    private final Context context;
    private final AudioStuffFactory audioStuffFactory;
    private final Speaker speaker;

    public StudyingPracticeWordSetInteractor(WordsCombinator wordsCombinator,
                                             SentenceProvider sentenceProvider,
                                             SentenceSelector sentenceSelector,
                                             RefereeService refereeService,
                                             Logger logger,
                                             WordSetExperienceService experienceService,
                                             PracticeWordSetExerciseService exerciseService,
                                             Context context,
                                             AudioStuffFactory audioStuffFactory,
                                             Speaker speaker) {
        this.wordsCombinator = wordsCombinator;
        this.sentenceProvider = sentenceProvider;
        this.sentenceSelector = sentenceSelector;
        this.refereeService = refereeService;
        this.logger = logger;
        this.experienceService = experienceService;
        this.exerciseService = exerciseService;
        this.context = context;
        this.audioStuffFactory = audioStuffFactory;
        this.speaker = speaker;
    }

    @Override
    public void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener) {
        WordSetExperience exp = experienceService.findById(wordSet.getId());
        logger.i(TAG, "find experience by id {}, word set {}", exp, wordSet);
        if (exp == null) {
            logger.i(TAG, "create new experience");
            exp = experienceService.createNew(wordSet);
        }
        if (REPETITION.equals(exp.getStatus())) {
            logger.i(TAG, "enable repetition mode");
            sentenceProvider.enableRepetitionMode();
            listener.onEnableRepetitionMode();
        } else {
            logger.i(TAG, "disable repetition mode for state {} ", exp.getStatus());
            sentenceProvider.disableRepetitionMode();
            listener.onDisableRepetitionMode();
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
        final Sentence sentence = sentenceSelector.getSentence(sentences);
        logger.i(TAG, "chosen sentence {}", sentences);
        exerciseService.save(word, wordSetId, sentence);
        listener.onSentencesFound(sentence, word);
        logger.i(TAG, "sentence was initialized");
    }

    @Override
    public void checkAnswer(String answer, final WordSet wordSet, final Sentence sentence, final OnPracticeWordSetListener listener) {
        logger.i(TAG, "check answer {} ", answer);
        if (isEmpty(answer)) {
            logger.i(TAG, "answer is empty");
            listener.onAnswerEmpty();
            return;
        }
        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setWordSetExperienceId(wordSet.getId());
        uncheckedAnswer.setActualAnswer(answer);
        uncheckedAnswer.setExpectedAnswer(sentence.getText());

        logger.i(TAG, "checking ... {}", uncheckedAnswer);
        AnswerCheckingResult result = refereeService.checkAnswer(uncheckedAnswer);
        if (!result.getErrors().isEmpty()) {
            logger.i(TAG, "errors were found ... {}", result.getErrors());
            listener.onSpellingOrGrammarError(result.getErrors());
            return;
        }

        if (result.isAccuracyTooLow()) {
            logger.i(TAG, "accuracy is too low");
            listener.onAccuracyTooLowError();
            return;
        }

        logger.i(TAG, "accuracy is ok");
        WordSetExperience exp = experienceService.increaseExperience(wordSet.getId(), 1);
        logger.i(TAG, "experience is {}", exp);
        listener.onUpdateProgress(exp);

        exerciseService.moveCurrentWordToNextState(wordSet.getId());
        if (exp.getTrainingExperience() == exp.getMaxTrainingExperience() / 2) {
            logger.i(TAG, "training half finished");
            experienceService.moveToAnotherState(wordSet.getId(), REPETITION);
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
    }

    @Override
    public void playVoice(Uri voiceRecordUri, OnPracticeWordSetListener listener) {
        if (voiceRecordUri == null) {
            logger.i(TAG, "voice record uri is empty");
            return;
        }
        MediaPlayer mp = null;
        try {
            listener.onStartPlaying();
            try {
                mp = audioStuffFactory.createMediaPlayer();
                mp.setDataSource(context, voiceRecordUri);
                mp.prepare();
                mp.start();
                logger.i(TAG, "start playing {}", voiceRecordUri);
                while (mp.isPlaying()) {
                    logger.i(TAG, "playing...");
                    Thread.sleep(500);
                }
                logger.i(TAG, "stop playing");
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } finally {
            listener.onStopPlaying();
        }
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

    @Override
    public void pronounceRightAnswer(int wordSetId, OnPracticeWordSetListener listener) {
        Sentence currentSentence = exerciseService.getCurrentSentence(wordSetId);
        if (currentSentence == null) {
            return;
        }
        logger.i(TAG, "start speaking {}", currentSentence.getText());
        try {
            speaker.speak(currentSentence.getText());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        logger.i(TAG, "stop speaking");
    }

    @Override
    public void rightAnswerUntouched(int wordSetId, OnPracticeWordSetListener listener) {
        boolean answered = exerciseService.isCurrentExerciseAnswered(wordSetId);
        if (answered) {
            return;
        }
        Sentence currentSentence = exerciseService.getCurrentSentence(wordSetId);
        Word2Tokens currentWord = exerciseService.getCurrentWord(wordSetId);
        listener.onHideRightAnswer(currentSentence, currentWord);
    }
}