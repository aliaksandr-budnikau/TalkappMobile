package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.service.AudioProcessesFactory;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.ByteUtils;
import talkapp.org.talkappmobile.service.RecordedTrack;
import talkapp.org.talkappmobile.service.impl.AudioProcessesFactoryImpl;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryImpl;
import talkapp.org.talkappmobile.service.impl.ByteUtilsImpl;
import talkapp.org.talkappmobile.service.impl.RecordedTrackImpl;

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
    public RecordedTrack provideRecordedTrackBuffer(ByteUtils byteUtils) {
        return new RecordedTrackImpl(byteUtils);
    }

    @Provides
    @Singleton
    public AudioProcessesFactory provideAudioProcessesFactory(AudioStuffFactory audioStuffFactory, ByteUtils byteUtils) {
        return new AudioProcessesFactoryImpl(audioStuffFactory, byteUtils);
    }
}