package talkapp.org.talkappmobile.service.impl;

import android.media.AudioRecord;

import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.ByteUtils;
import talkapp.org.talkappmobile.service.RecordedTrack;

/**
 * @author Budnikau Aliaksandr
 */
public class VoiceRecordingProcess {

    private static final int SPEECH_TIMEOUT_MILLIS = 2000;
    private static final int MAX_SPEECH_LENGTH_MILLIS = 30 * 1000;

    private final RecordedTrack recordedTrackBuffer;
    private final ByteUtils byteUtils;
    private final AudioStuffFactory audioStuffFactory;
    private boolean recording = false;

    public VoiceRecordingProcess(RecordedTrack recordedTrackBuffer, AudioStuffFactory audioStuffFactory, ByteUtils byteUtils) {
        this.recordedTrackBuffer = recordedTrackBuffer;
        this.audioStuffFactory = audioStuffFactory;
        this.byteUtils = byteUtils;
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
                final int size = audioRecord.read(buffer, 0, buffer.length);
                final long now = System.currentTimeMillis();
                if (byteUtils.isHearingVoice(buffer, size)) {
                    if (lastVoiceHeardMillis == Long.MAX_VALUE) {
                        voiceStartedMillis = now;
                    }
                    recordedTrackBuffer.append(buffer);
                    lastVoiceHeardMillis = now;
                    if (now - voiceStartedMillis > MAX_SPEECH_LENGTH_MILLIS) {
                        lastVoiceHeardMillis = Long.MAX_VALUE;
                    }
                } else if (lastVoiceHeardMillis != Long.MAX_VALUE) {
                    recordedTrackBuffer.append(buffer);
                    if (now - lastVoiceHeardMillis > SPEECH_TIMEOUT_MILLIS) {
                        lastVoiceHeardMillis = Long.MAX_VALUE;
                    }
                }
            }
        } finally {
            audioRecord.release();
            recording = false;
        }
    }

    public void stop() {
        recording = false;
    }
}