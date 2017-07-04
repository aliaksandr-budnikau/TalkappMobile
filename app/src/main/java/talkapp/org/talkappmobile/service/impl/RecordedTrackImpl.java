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

    private List<Byte> track = new LinkedList<>();

    public RecordedTrackImpl(ByteUtils byteUtils) {
        this.byteUtils = byteUtils;
    }

    @Override
    public byte[] get() {
        return byteUtils.toPrimitives(track);
    }

    @Override
    public int size() {
        return track.size();
    }

    @Override
    public void clear() {
        track.clear();
    }

    @Override
    public void append(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            track.add(data[i]);
        }
    }
}