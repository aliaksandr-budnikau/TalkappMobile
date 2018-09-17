package talkapp.org.talkappmobile.component;

/**
 * @author Budnikau Aliaksandr
 */
public interface RecordedTrack {

    byte[] getAsOneArray();

    boolean isEmpty();

    int getPosition();

    void init();

    void append(byte[] data);

    int getMaxSpeechLengthMillis();

    void close();

    boolean isClosed();
}