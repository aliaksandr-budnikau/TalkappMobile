package talkapp.org.talkappmobile.service.impl;

import android.media.AudioTrack;

import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.RecordedTrack;

/**
 * @author Budnikau Aliaksandr
 */
public class VoicePlayingProcess {

    private final RecordedTrack recordedTrackBuffer;
    private final AudioStuffFactory audioStuffFactory;

    public VoicePlayingProcess(RecordedTrack recordedTrackBuffer, AudioStuffFactory audioStuffFactory) {
        this.recordedTrackBuffer = recordedTrackBuffer;
        this.audioStuffFactory = audioStuffFactory;
    }

    public void play() {
        AudioTrack audioTrack = null;
        try {
            audioTrack = audioStuffFactory.createAudioTrack();
            audioTrack.play();
            audioTrack.write(recordedTrackBuffer.get(), 0, recordedTrackBuffer.size());
        } finally {
            if (audioTrack != null) {
                audioTrack.release();
            }
        }
    }
}