package talkapp.org.talkappmobile.activity.interactor;

import talkapp.org.talkappmobile.activity.listener.ExceptionHandlerListner;
import talkapp.org.talkappmobile.component.Logger;

public class ExceptionHandlerInteractor {
    private static final String TAG = ExceptionHandlerInteractor.class.getSimpleName();

    private final Logger logger;

    public ExceptionHandlerInteractor(Logger logger) {
        this.logger = logger;
    }

    public void handleInternetConnectionLostException(ExceptionHandlerListner listner) {
        listner.onInternetConnectionLost();
    }

    public void handleAuthorizationException(ExceptionHandlerListner listner) {
        listner.onUnauthorizedAccess();
    }

    public void handleUncaughtException(ExceptionHandlerListner listner, Throwable e) {
        logger.e(TAG, e, e.getMessage());
        listner.onUncaughtException(e);
    }
}