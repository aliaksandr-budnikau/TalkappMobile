package talkapp.org.talkappmobile.service.impl;

public class InternetConnectionLostException extends RuntimeException {
    public InternetConnectionLostException(String message, Exception e) {
        super(message, e);
    }
}