package talkapp.org.talkappmobile.component.impl;

import java.nio.ByteBuffer;

import talkapp.org.talkappmobile.component.RecordedTrack;

/**
 * @author Budnikau Aliaksandr
 */
public class RecordedTrackImpl implements RecordedTrack {
    private final int maxSpeechLengthMillis;
    private final int byteBufferSize;
    private ByteBuffer byteBuf;
    private boolean closed;

    public RecordedTrackImpl(int maxSpeechLengthMillis) {
        this.maxSpeechLengthMillis = maxSpeechLengthMillis;
        this.byteBufferSize = 35 * maxSpeechLengthMillis;
        byteBuf = ByteBuffer.allocate(byteBufferSize);
    }

    @Override
    public byte[] getAsOneArray() {
        return byteBuf.array();
    }

    @Override
    public boolean isEmpty() {
        return byteBuf.position() == 0;
    }

    @Override
    public int getPosition() {
        return byteBuf.position();
    }

    @Override
    public void init() {
        byteBuf = ByteBuffer.allocate(byteBufferSize);
        closed = false;
    }

    @Override
    public void append(byte[] data) {
        byteBuf.put(data);
    }

    @Override
    public int getMaxSpeechLengthMillis() {
        return maxSpeechLengthMillis;
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}