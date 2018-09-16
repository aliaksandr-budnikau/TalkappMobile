package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.AudioProcessesFactory;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.ByteUtils;
import talkapp.org.talkappmobile.component.RecordedTrack;
import talkapp.org.talkappmobile.component.impl.AudioProcessesFactoryImpl;
import talkapp.org.talkappmobile.component.impl.AudioStuffFactoryImpl;
import talkapp.org.talkappmobile.component.impl.ByteUtilsImpl;
import talkapp.org.talkappmobile.component.impl.RecordedTrackImpl;

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

    @Provides
    @Singleton
    public RecordedTrack provideRecordedTrackBuffer() {
        return new RecordedTrackImpl();
    }

    @Provides
    @Singleton
    public AudioProcessesFactory provideAudioProcessesFactory(AudioStuffFactory audioStuffFactory, ByteUtils byteUtils) {
        return new AudioProcessesFactoryImpl(audioStuffFactory, byteUtils);
    }
}