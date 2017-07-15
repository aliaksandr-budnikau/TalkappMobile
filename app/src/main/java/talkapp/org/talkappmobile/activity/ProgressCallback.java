package talkapp.org.talkappmobile.activity;

/**
 * @author Budnikau Aliaksandr
 */
public interface ProgressCallback {
    void markProgress(long speechLength, long maxSpeechLengthMillis);
}