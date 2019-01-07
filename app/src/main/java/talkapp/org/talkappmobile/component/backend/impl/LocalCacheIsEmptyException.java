package talkapp.org.talkappmobile.component.backend.impl;

public class LocalCacheIsEmptyException extends RuntimeException {
    public LocalCacheIsEmptyException(String message) {
        super(message);
    }
}