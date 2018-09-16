package talkapp.org.talkappmobile.component;

/**
 * @author Budnikau Aliaksandr
 */
public interface RecordedTrack {

    byte[] getAsOneArray();

    boolean isEmpty();

    void clear();

    void append(byte[] data);
}