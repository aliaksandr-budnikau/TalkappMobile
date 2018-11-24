package talkapp.org.talkappmobile.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.impl.AudioStuffFactoryImpl;
import talkapp.org.talkappmobile.component.impl.SpeakerImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class AudioModule {

    @Provides
    @Singleton
    public AudioStuffFactory provideAudioStuffFactory() {
        return new AudioStuffFactoryImpl();
    }

    @Provides
    @Singleton
    public Speaker provideSpeaker(Context context) {
        return new SpeakerImpl(context);
    }
}