package talkapp.org.talkappmobile.service;

public class LocalCacheIsEmptyException extends RuntimeException {
    public LocalCacheIsEmptyException(String message) {
        super(message);
    }
}