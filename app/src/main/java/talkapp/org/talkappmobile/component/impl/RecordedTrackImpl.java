package talkapp.org.talkappmobile.component.impl;

import java.nio.ByteBuffer;

import talkapp.org.talkappmobile.component.RecordedTrack;

import static talkapp.org.talkappmobile.component.impl.VoiceRecordingProcess.MAX_SPEECH_LENGTH_MILLIS;

/**
 * @author Budnikau Aliaksandr
 */
public class RecordedTrackImpl implements RecordedTrack {
    private static final int BYTE_BUFFER_SIZE = 35 * MAX_SPEECH_LENGTH_MILLIS;
    private ByteBuffer byteBuf = ByteBuffer.allocate(BYTE_BUFFER_SIZE);

    @Override
    public byte[] getAsOneArray() {
        return byteBuf.array();
    }

    @Override
    public boolean isEmpty() {
        return byteBuf.position() == 0;
    }

    @Override
    public void clear() {
        byteBuf = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
    }

    @Override
    public void append(byte[] data) {
        byteBuf.put(data);
    }
}