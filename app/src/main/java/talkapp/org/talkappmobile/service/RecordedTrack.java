package talkapp.org.talkappmobile.service;

import java.util.List;

/**
 * @author Budnikau Aliaksandr
 */
public interface RecordedTrack {

    byte[] getAsOneArray();

    List<byte[]> get();

    boolean isEmpty();

    void clear();

    void append(byte[] data);
}