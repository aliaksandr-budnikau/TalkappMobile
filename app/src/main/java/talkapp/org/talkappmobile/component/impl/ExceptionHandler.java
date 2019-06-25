package talkapp.org.talkappmobile.component.impl;

import org.talkappmobile.service.impl.InternetConnectionLostException;
import org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import java.lang.Thread.UncaughtExceptionHandler;

import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.presenter.ExceptionHandlerPresenter;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;

public class ExceptionHandler implements UncaughtExceptionHandler {

    private final ExceptionHandlerPresenter presenter;

    public ExceptionHandler(ExceptionHandlerView view, ExceptionHandlerInteractor interactor) {
        presenter = new ExceptionHandlerPresenter(view, interactor);
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