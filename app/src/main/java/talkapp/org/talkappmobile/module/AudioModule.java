package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.ByteUtils;
import talkapp.org.talkappmobile.component.RecordedTrack;
import talkapp.org.talkappmobile.component.impl.AudioStuffFactoryImpl;
import talkapp.org.talkappmobile.component.impl.ByteUtilsImpl;
import talkapp.org.talkappmobile.component.impl.RecordedTrackImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class AudioModule {
    private static final int MAX_SPEECH_LENGTH_MILLIS = 8 * 1000;

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
        return new RecordedTrackImpl(MAX_SPEECH_LENGTH_MILLIS);
    }
}