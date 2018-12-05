package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.model.WordSet;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public abstract class AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = AbstractPracticeWordSetInteractor.class.getSimpleName();
    private final Logger logger;
    private final Context context;
    private final RefereeService refereeService;
    private final AudioStuffFactory audioStuffFactory;
    private final Speaker speaker;

    public AbstractPracticeWordSetInteractor(Logger logger,
                                             Context context,
                                             RefereeService refereeService,
                                             AudioStuffFactory audioStuffFactory,
                                             Speaker speaker) {
        this.logger = logger;
        this.context = context;
        this.refereeService = refereeService;
        this.audioStuffFactory = audioStuffFactory;
        this.speaker = speaker;
    }

    @Override
    public boolean checkAnswer(String answer, WordSet wordSet, Sentence sentence, OnPracticeWordSetListener listener) {
        logger.i(TAG, "check answer {} ", answer);
        if (isEmpty(answer)) {
            logger.i(TAG, "answer is empty");
            listener.onAnswerEmpty();
            return false;
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
            return false;
        }

        if (result.isAccuracyTooLow()) {
            logger.i(TAG, "accuracy is too low");
            listener.onAccuracyTooLowError();
            return false;
        }
        logger.i(TAG, "accuracy is ok");
        return true;
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
    public void pronounceRightAnswer(Sentence sentence, OnPracticeWordSetListener listener) {
        logger.i(TAG, "start speaking {}", sentence.getText());
        try {
            speaker.speak(sentence.getText());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        logger.i(TAG, "stop speaking");
    }
}