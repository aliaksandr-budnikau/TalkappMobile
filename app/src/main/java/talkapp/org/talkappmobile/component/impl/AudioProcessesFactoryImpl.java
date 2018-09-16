package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.activity.ProgressCallback;
import talkapp.org.talkappmobile.component.AudioProcessesFactory;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.ByteUtils;
import talkapp.org.talkappmobile.component.RecordedTrack;

/**
 * @author Budnikau Aliaksandr
 */
public class AudioProcessesFactoryImpl implements AudioProcessesFactory {

    private final AudioStuffFactory audioStuffFactory;
    private final ByteUtils byteUtils;

    public AudioProcessesFactoryImpl(AudioStuffFactory audioStuffFactory, ByteUtils byteUtils) {
        this.audioStuffFactory = audioStuffFactory;
        this.byteUtils = byteUtils;
    }

    @Override
    public VoiceRecordingProcess createVoiceRecordingProcess(RecordedTrack recordedTrackBuffer, ProgressCallback progress) {
        return new VoiceRecordingProcess(recordedTrackBuffer, audioStuffFactory, byteUtils, progress);
    }
}