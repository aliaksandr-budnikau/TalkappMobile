package talkapp.org.talkappmobile.service.impl;

public class LocalCacheIsEmptyException extends RuntimeException {
    public LocalCacheIsEmptyException(String message) {
        super(message);
    }
}