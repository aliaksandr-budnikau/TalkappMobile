package talkapp.org.talkappmobile.service;

/**
 * @author Budnikau Aliaksandr
 */
public interface RecordedTrack {

    byte[] get();

    int size();

    void clear();

    void append(byte[] data);
}