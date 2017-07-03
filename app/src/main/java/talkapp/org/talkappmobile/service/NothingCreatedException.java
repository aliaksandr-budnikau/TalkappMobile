package talkapp.org.talkappmobile.service;

/**
 * @author Budnikau Aliaksandr
 */
public class NothingCreatedException extends RuntimeException {
    public NothingCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NothingCreatedException(String message) {
        super(message);
    }
}