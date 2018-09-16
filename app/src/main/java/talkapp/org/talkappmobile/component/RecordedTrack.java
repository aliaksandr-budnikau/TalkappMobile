package talkapp.org.talkappmobile.component;

/**
 * @author Budnikau Aliaksandr
 */
public interface RecordedTrack {

    byte[] getAsOneArray();

    boolean isEmpty();

    int getPosition();

    void clear();

    void append(byte[] data);
}