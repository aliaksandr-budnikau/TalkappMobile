package talkapp.org.talkappmobile.component.impl;

import java.lang.Thread.UncaughtExceptionHandler;

import talkapp.org.talkappmobile.presenter.ExceptionHandlerPresenter;

public class ExceptionHandler implements UncaughtExceptionHandler {

    private final ExceptionHandlerPresenter presenter;

    public ExceptionHandler(ExceptionHandlerPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Throwable cause = getCause(e);
        presenter.handleUncaughtException(t, e, cause);
    }

    private Throwable getCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}