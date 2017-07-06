package talkapp.org.talkappmobile.service;

/**
 * @author Budnikau Aliaksandr
 */
public class NothingGotException extends RuntimeException {
    public NothingGotException(String message, Throwable cause) {
        super(message, cause);
    }

    public NothingGotException(String message) {
        super(message);
    }

    public NothingGotException(Throwable cause) {
        super(cause);
    }
}