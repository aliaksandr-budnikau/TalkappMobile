package talkapp.org.talkappmobile.component.impl;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.NothingGotException;

/**
 * @author Budnikau Aliaksandr
 */
public class AudioStuffFactoryImpl implements AudioStuffFactory {

    private static final int[] SAMPLE_RATE_CANDIDATES = new int[]{16000, 11025, 22050, 44100};
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private byte[] buffer;

    @Override
    public byte[] createBuffer() throws NothingGotException {
        if (buffer == null) {
            throw new NothingGotException("Cannot instantiate Buffer");
        }
        return buffer;
    }

    @Override
    public AudioRecord createAudioRecord() throws NothingGotException {
        try {
            for (int sampleRate : SAMPLE_RATE_CANDIDATES) {
                final int sizeInBytes = AudioRecord.getMinBufferSize(sampleRate, CHANNEL, ENCODING);
                if (sizeInBytes == AudioRecord.ERROR_BAD_VALUE) {
                    continue;
                }
                final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        sampleRate, CHANNEL, ENCODING, sizeInBytes);
                if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                    buffer = new byte[sizeInBytes];
                    return audioRecord;
                } else {
                    audioRecord.release();
                }
            }
        } catch (Exception e) {
            throw new NothingGotException("Cannot instantiate AudioRecord", e);
        }
        throw new NothingGotException("Cannot instantiate AudioRecord");
    }

    @Override
    public AudioTrack createAudioTrack() throws NothingGotException {
        try {
            for (int sampleRate : SAMPLE_RATE_CANDIDATES) {
                final int sizeInBytes = AudioTrack.getMinBufferSize(sampleRate, CHANNEL, ENCODING);
                if (sizeInBytes == AudioTrack.ERROR_BAD_VALUE) {
                    continue;
                }
                final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, ENCODING, sizeInBytes, AudioTrack.MODE_STREAM);
                if (audioTrack.getState() == AudioRecord.STATE_INITIALIZED) {
                    return audioTrack;
                } else {
                    audioTrack.release();
                }
            }
        } catch (Exception e) {
            throw new NothingGotException("Cannot instantiate AudioTrack", e);
        }
        throw new NothingGotException("Cannot instantiate AudioTrack");
    }
}