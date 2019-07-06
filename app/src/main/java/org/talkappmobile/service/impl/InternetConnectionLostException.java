package org.talkappmobile.service.impl;

public class InternetConnectionLostException extends RuntimeException {
    public InternetConnectionLostException(String message) {
        super(message);
    }
}