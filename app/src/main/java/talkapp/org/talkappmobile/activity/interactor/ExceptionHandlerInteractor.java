package talkapp.org.talkappmobile.activity.interactor;

import talkapp.org.talkappmobile.activity.listener.ExceptionHandlerListner;

public class ExceptionHandlerInteractor {
    public void handleInternetConnectionLostException(ExceptionHandlerListner listner) {
        listner.onInternetConnectionLost();
    }

    public void handleAuthorizationException(ExceptionHandlerListner listner) {
        listner.onUnauthorizedAccess();
    }

    public void handleUncaughtException(ExceptionHandlerListner listner, Throwable e) {
        listner.onUncaughtException(e);
    }
}