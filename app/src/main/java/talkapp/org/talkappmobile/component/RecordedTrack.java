package talkapp.org.talkappmobile.component;

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