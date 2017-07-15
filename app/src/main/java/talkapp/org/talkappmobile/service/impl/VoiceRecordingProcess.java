package talkapp.org.talkappmobile.service.impl;

import android.media.AudioRecord;

import talkapp.org.talkappmobile.activity.ProgressCallback;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.ByteUtils;
import talkapp.org.talkappmobile.service.RecordedTrack;

/**
 * @author Budnikau Aliaksandr
 */
public class VoiceRecordingProcess {

    private static final int SPEECH_TIMEOUT_MILLIS = 2000;
    private static final int MAX_SPEECH_LENGTH_MILLIS = 8 * 1000;
    private static final int MAX_SPEECH_LENGTH_MILLIS_NORMALISED = MAX_SPEECH_LENGTH_MILLIS / 100;

    private final RecordedTrack recordedTrackBuffer;
    private final ByteUtils byteUtils;
    private final AudioStuffFactory audioStuffFactory;
    private final ProgressCallback progress;
    private boolean recording = false;

    public VoiceRecordingProcess(RecordedTrack recordedTrackBuffer,
                                 AudioStuffFactory audioStuffFactory, ByteUtils byteUtils,
                                 ProgressCallback progress) {
        this.recordedTrackBuffer = recordedTrackBuffer;
        this.audioStuffFactory = audioStuffFactory;
        this.byteUtils = byteUtils;
        this.progress = progress;
    }

    public void rec() {
        recordedTrackBuffer.clear();
        recording = true;
        AudioRecord audioRecord = null;
        try {
            audioRecord = audioStuffFactory.createAudioRecord();
            audioRecord.startRecording();
            byte[] buffer = audioStuffFactory.createBuffer();
            long voiceStartedMillis = 0;
            long lastVoiceHeardMillis = Long.MAX_VALUE;
            while (recording) {
                buffer = new byte[buffer.length];
                final int size = audioRecord.read(buffer, 0, buffer.length);
                final long now = System.currentTimeMillis();
                if (byteUtils.isHearingVoice(buffer, size)) {
                    if (lastVoiceHeardMillis == Long.MAX_VALUE) {
                        voiceStartedMillis = now;
                    }
                    recordedTrackBuffer.append(buffer);
                    lastVoiceHeardMillis = now;
                    long speechLength = now - voiceStartedMillis;
                    progress.markProgress(speechLength, MAX_SPEECH_LENGTH_MILLIS_NORMALISED);
                    if (speechLength > MAX_SPEECH_LENGTH_MILLIS) {
                        recording = false;
                    }
                } else {
                    recordedTrackBuffer.append(buffer);
                    if (now - lastVoiceHeardMillis > SPEECH_TIMEOUT_MILLIS) {
                        recording = false;
                    }
                }
            }
        } finally {
            audioRecord.release();
        }
    }

    public void stop() {
        recording = false;
    }
}