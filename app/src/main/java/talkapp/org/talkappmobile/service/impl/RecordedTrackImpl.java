package talkapp.org.talkappmobile.service.impl;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.service.ByteUtils;
import talkapp.org.talkappmobile.service.RecordedTrack;

/**
 * @author Budnikau Aliaksandr
 */
public class RecordedTrackImpl implements RecordedTrack {
    private ByteUtils byteUtils;

    private List<byte[]> track = new LinkedList<>();

    public RecordedTrackImpl(ByteUtils byteUtils) {
        this.byteUtils = byteUtils;
    }

    @Override
    public byte[] getAsOneArray() {
        LinkedList<Byte> result = new LinkedList<>();
        for (byte[] bytes : track) {
            for (int i = 0; i < bytes.length; i++) {
                result.add(bytes[i]);
            }
        }
        return byteUtils.toPrimitives(result);
    }

    @Override
    public List<byte[]> get() {
        return track;
    }

    @Override
    public boolean isEmpty() {
        return track.isEmpty();
    }

    @Override
    public void clear() {
        track.clear();
    }

    @Override
    public void append(byte[] data) {
        track.add(data);
    }
}