package talkapp.org.talkappmobile.service.impl;

import android.media.MediaPlayer;

import talkapp.org.talkappmobile.service.AudioStuffFactory;

/**
 * @author Budnikau Aliaksandr
 */
public class AudioStuffFactoryImpl implements AudioStuffFactory {

    @Override
    public MediaPlayer createMediaPlayer() {
        return new MediaPlayer();
    }
}