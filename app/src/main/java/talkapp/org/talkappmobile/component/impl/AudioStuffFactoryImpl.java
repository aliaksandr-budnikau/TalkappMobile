package talkapp.org.talkappmobile.component.impl;

import android.media.MediaPlayer;

import talkapp.org.talkappmobile.component.AudioStuffFactory;

/**
 * @author Budnikau Aliaksandr
 */
public class AudioStuffFactoryImpl implements AudioStuffFactory {

    @Override
    public MediaPlayer createMediaPlayer() {
        return new MediaPlayer();
    }
}