package talkapp.org.talkappmobile.interactor;

import javax.inject.Inject;

import talkapp.org.talkappmobile.listener.ExceptionHandlerListner;
import talkapp.org.talkappmobile.service.InternetConnectionLostException;
import talkapp.org.talkappmobile.service.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.service.Logger;

public class ExceptionHandlerInteractor {
    private static final String TAG = ExceptionHandlerInteractor.class.getSimpleName();

    private final Logger logger;

    @Inject
    public ExceptionHandlerInteractor(Logger logger) {
        this.logger = logger;
    }

    public void handleInternetConnectionLostException(ExceptionHandlerListner listner) {
        listner.onInternetConnectionLost();
    }

    public void handleUncaughtException(ExceptionHandlerListner listner, Thread t, Throwable e, Throwable cause) {
        if (cause instanceof InternetConnectionLostException) {
            handleInternetConnectionLostException(listner);
        } else if (cause instanceof LocalCacheIsEmptyException) {
            handleLocalCacheIsEmptyException(listner);
        } else {
            logger.e(TAG, e, e.getMessage());
            listner.onUncaughtException(e);
        }
    }

    public void handleLocalCacheIsEmptyException(ExceptionHandlerListner listner) {
        listner.onLocalCacheIsEmpty();
    }
}