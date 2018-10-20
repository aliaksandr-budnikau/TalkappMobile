package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.REPETITION;

public class PracticeWordSetInteractor {
    private static final String TAG = PracticeWordSetInteractor.class.getSimpleName();
    private WordsCombinator wordsCombinator;
    private SentenceProvider sentenceProvider;
    private SentenceSelector sentenceSelector;
    private RefereeService refereeService;
    private Logger logger;
    private WordSetExperienceRepository experienceRepository;
    private PracticeWordSetExerciseRepository exerciseRepository;
    private Context context;
    private AudioStuffFactory audioStuffFactory;

    public PracticeWordSetInteractor(WordsCombinator wordsCombinator, SentenceProvider sentenceProvider, SentenceSelector sentenceSelector, RefereeService refereeService, Logger logger, WordSetExperienceRepository experienceRepository, PracticeWordSetExerciseRepository exerciseRepository, Context context, AudioStuffFactory audioStuffFactory) {
        this.wordsCombinator = wordsCombinator;
        this.sentenceProvider = sentenceProvider;
        this.sentenceSelector = sentenceSelector;
        this.refereeService = refereeService;
        this.logger = logger;
        this.experienceRepository = experienceRepository;
        this.exerciseRepository = exerciseRepository;
        this.context = context;
        this.audioStuffFactory = audioStuffFactory;
    }

    public void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener) {
        WordSetExperience exp = experienceRepository.findById(wordSet.getId());
        if (exp == null) {
            exp = experienceRepository.createNew(wordSet);
        }
        if (REPETITION.equals(exp.getStatus())) {
            sentenceProvider.enableRepetitionMode();
            listener.onEnableRepetitionMode();
        } else {
            sentenceProvider.disableRepetitionMode();
            listener.onDisableRepetitionMode();
        }
        listener.onInitialiseExperience(exp);
    }

    public void initialiseWordsSequence(WordSet wordSet, OnPracticeWordSetListener listener) {
        Set<String> words = wordsCombinator.combineWords(wordSet.getWords());
        exerciseRepository.createSomeIfNecessary(words, wordSet.getId());
    }

    public void initialiseSentence(String word, String wordSetId, final OnPracticeWordSetListener listener) {
        List<Sentence> sentences = sentenceProvider.findByWordAndWordSetId(word, wordSetId);
        if (sentences.isEmpty()) {
            logger.w(TAG, "Sentences haven't been found with words '{}'. Fill the storage.", word);
            return;
        }
        final Sentence sentence = sentenceSelector.getSentence(sentences);
        exerciseRepository.save(word, wordSetId, sentence);
        listener.onSentencesFound(sentence, word);
    }

    public void checkAnswer(String answer, final WordSet wordSet, final Sentence sentence, final OnPracticeWordSetListener listener) {
        if (isEmpty(answer)) {
            listener.onAnswerEmpty();
            return;
        }
        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setWordSetExperienceId(wordSet.getId());
        uncheckedAnswer.setActualAnswer(answer);
        uncheckedAnswer.setExpectedAnswer(sentence.getText());

        AnswerCheckingResult result = refereeService.checkAnswer(uncheckedAnswer);
        if (!result.getErrors().isEmpty()) {
            listener.onSpellingOrGrammarError(result.getErrors());
            return;
        }

        if (result.isAccuracyTooLow()) {
            listener.onAccuracyTooLowError();
            return;
        }

        WordSetExperience exp = experienceRepository.increaseExperience(wordSet.getId(), 1);
        listener.onUpdateProgress(exp);

        exerciseRepository.moveCurrentWordToNextState(wordSet.getId());
        exerciseRepository.putOffCurrentWord(wordSet.getId());
        if (exp.getTrainingExperience() == exp.getMaxTrainingExperience() / 2) {
            experienceRepository.moveToAnotherState(wordSet.getId(), REPETITION);
            sentenceProvider.enableRepetitionMode();
            listener.onTrainingHalfFinished(sentence);
            listener.onEnableRepetitionMode();
        } else if (exp.getTrainingExperience() == exp.getMaxTrainingExperience()) {
            experienceRepository.moveToAnotherState(wordSet.getId(), FINISHED);
            listener.onTrainingFinished();
        } else {
            listener.onRightAnswer(sentence);
        }
    }

    public void playVoice(Uri voiceRecordUri, OnPracticeWordSetListener listener) {
        if (voiceRecordUri == null) {
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
                while (mp.isPlaying()) {
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } finally {
            listener.onStopPlaying();
        }
    }

    public Sentence getCurrentSentence(String wordSetId) {
        return exerciseRepository.getCurrentSentence(wordSetId);
    }

    public String peekByWordSetIdAnyWord(String wordSetId) {
        return exerciseRepository.peekByWordSetIdAnyWord(wordSetId);
    }

    public String getCurrentWord(String wordSetId) {
        return exerciseRepository.getCurrentWord(wordSetId);
    }
}