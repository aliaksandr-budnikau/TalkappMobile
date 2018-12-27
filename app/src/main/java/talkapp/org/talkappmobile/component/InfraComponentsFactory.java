package talkapp.org.talkappmobile.component;

import java.lang.Thread.UncaughtExceptionHandler;

import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;

public interface InfraComponentsFactory {
    UncaughtExceptionHandler createExceptionHandler(ExceptionHandlerView view, ExceptionHandlerInteractor interactor);
}