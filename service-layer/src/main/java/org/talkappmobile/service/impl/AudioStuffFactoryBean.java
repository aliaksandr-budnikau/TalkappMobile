package org.talkappmobile.service.impl;

import android.media.MediaPlayer;

import org.androidannotations.annotations.EBean;
import org.talkappmobile.service.AudioStuffFactory;

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