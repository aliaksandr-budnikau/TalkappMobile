package talkapp.org.talkappmobile.component.impl;

import android.media.MediaPlayer;

import org.androidannotations.annotations.EBean;

import talkapp.org.talkappmobile.component.AudioStuffFactory;

/**
 * @author Budnikau Aliaksandr
 */
@EBean(scope = EBean.Scope.Singleton)
public class AudioStuffFactoryBean implements AudioStuffFactory {

    @Override
    public MediaPlayer createMediaPlayer() {
        return new MediaPlayer();
    }
}