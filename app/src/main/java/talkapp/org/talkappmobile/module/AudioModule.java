package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.ByteUtils;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryImpl;
import talkapp.org.talkappmobile.service.impl.ByteUtilsImpl;

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
    public ByteUtils provideByteUtils() {
        return new ByteUtilsImpl();
    }
}