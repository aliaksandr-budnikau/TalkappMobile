package talkapp.org.talkappmobile.component.impl;

import android.media.AudioTrack;

import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.RecordedTrack;

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
            byte[] bytes = recordedTrackBuffer.getAsOneArray();
            audioTrack.write(bytes, 0, bytes.length);
        } finally {
            if (audioTrack != null) {
                audioTrack.release();
            }
        }
    }
}