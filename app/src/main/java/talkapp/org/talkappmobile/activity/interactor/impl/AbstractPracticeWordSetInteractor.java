package talkapp.org.talkappmobile.activity.interactor.impl;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.listener.OnPracticeWordSetListener;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.model.Sentence;

public abstract class AbstractPracticeWordSetInteractor implements PracticeWordSetInteractor {
    private static final String TAG = AbstractPracticeWordSetInteractor.class.getSimpleName();
    private final Logger logger;
    private final Context context;
    private final AudioStuffFactory audioStuffFactory;
    private final Speaker speaker;

    public AbstractPracticeWordSetInteractor(Logger logger,
                                             Context context,
                                             AudioStuffFactory audioStuffFactory,
                                             Speaker speaker) {
        this.logger = logger;
        this.context = context;
        this.audioStuffFactory = audioStuffFactory;
        this.speaker = speaker;
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