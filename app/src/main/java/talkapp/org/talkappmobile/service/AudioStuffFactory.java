package talkapp.org.talkappmobile.service;

import android.media.AudioRecord;
import android.media.AudioTrack;

/**
 * @author Budnikau Aliaksandr
 */
public interface AudioStuffFactory {
    byte[] createBuffer();

    AudioRecord createAudioRecord();

    AudioTrack createAudioTrack();
}