package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.WordSet;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class PracticeWordSetInteractor {
    private static final String TAG = PracticeWordSetInteractor.class.getSimpleName();
    @Inject
    WordsCombinator wordsCombinator;
    @Inject
    SentenceProvider sentenceProvider;
    @Inject
    SentenceSelector sentenceSelector;
    @Inject
    RefereeService refereeService;
    @Inject
    Logger logger;
    @Inject
    WordSetExperienceRepository experienceRepository;
    @Inject
    Context context;
    @Inject
    AudioStuffFactory audioStuffFactory;

    public PracticeWordSetInteractor() {
        DIContext.get().inject(this);
    }

    public void initialiseExperience(WordSet wordSet, OnPracticeWordSetListener listener) {
        if (wordSet.getExperience() == null) {
            wordSet.setExperience(experienceRepository.createNew(wordSet));
        }
        listener.onInitialiseExperience();
    }

    public void initialiseWordsSequence(WordSet wordSet, OnPracticeWordSetListener listener) {
        Set<String> set = wordsCombinator.combineWords(wordSet.getWords());
        wordSet.setWords(new ArrayList<>(set));
    }

    public void initialiseSentence(String word, String wordSetId, final OnPracticeWordSetListener listener) {
        List<Sentence> sentences = sentenceProvider.findByWordAndWordSetId(word, wordSetId);
        if (sentences.isEmpty()) {
            logger.w(TAG, "Sentences haven't been found with words '{}'. Fill the storage.", word);
            return;
        }
        final Sentence sentence = sentenceSelector.getSentence(sentences);
        listener.onSentencesFound(sentence, word);
    }

    public void checkAnswer(String answer, final WordSet wordSet, final Sentence sentence, final OnPracticeWordSetListener listener) {
        if (isEmpty(answer)) {
            listener.onAnswerEmpty();
            return;
        }
        UncheckedAnswer uncheckedAnswer = new UncheckedAnswer();
        uncheckedAnswer.setWordSetExperienceId(wordSet.getExperience().getId());
        uncheckedAnswer.setActualAnswer(answer);
        uncheckedAnswer.setExpectedAnswer(sentence.getText());

        AnswerCheckingResult result = refereeService.checkAnswer(uncheckedAnswer);
        if (!result.getErrors().isEmpty()) {
            listener.onSpellingOrGrammarError(result.getErrors());
            return;
        }

        if (result.getCurrentTrainingExperience() == 0) {
            listener.onAccuracyTooLowError();
            return;
        }

        wordSet.getExperience().setTrainingExperience(result.getCurrentTrainingExperience());
        listener.onUpdateProgress(result.getCurrentTrainingExperience());

        if (result.getCurrentTrainingExperience() == wordSet.getExperience().getMaxTrainingExperience() / 2) {
            listener.onTrainingHalfFinished();
            return;
        }

        if (result.getCurrentTrainingExperience() == wordSet.getExperience().getMaxTrainingExperience()) {
            listener.onTrainingFinished();
            return;
        }
        listener.onRightAnswer();
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
}