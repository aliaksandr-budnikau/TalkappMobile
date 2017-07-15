package talkapp.org.talkappmobile.service.impl;

import talkapp.org.talkappmobile.activity.ProgressCallback;
import talkapp.org.talkappmobile.service.AudioProcessesFactory;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.ByteUtils;
import talkapp.org.talkappmobile.service.RecordedTrack;

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

    @Override
    public VoicePlayingProcess createVoicePlayingProcess(RecordedTrack recordedTrackBuffer) {
        return new VoicePlayingProcess(recordedTrackBuffer, audioStuffFactory);
    }
}