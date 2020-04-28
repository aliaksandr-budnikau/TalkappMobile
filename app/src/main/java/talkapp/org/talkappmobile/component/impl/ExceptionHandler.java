package talkapp.org.talkappmobile.component.impl;

import java.lang.Thread.UncaughtExceptionHandler;

import talkapp.org.talkappmobile.presenter.ExceptionHandlerPresenter;
import talkapp.org.talkappmobile.service.InternetConnectionLostException;
import talkapp.org.talkappmobile.service.LocalCacheIsEmptyException;

public class ExceptionHandler implements UncaughtExceptionHandler {

    private final ExceptionHandlerPresenter presenter;

    public ExceptionHandler(ExceptionHandlerPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Throwable cause = getCause(e);
        if (cause instanceof InternetConnectionLostException) {
            presenter.handleInternetConnectionLostException(t, e, cause);
        } else if (cause instanceof LocalCacheIsEmptyException) {
            presenter.handleLocalCacheIsEmptyException(t, e, cause);
        } else {
            presenter.handleUncaughtException(t, e, cause);
        }
    }

    private Throwable getCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}