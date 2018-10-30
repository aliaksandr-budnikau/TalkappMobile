package talkapp.org.talkappmobile.component;

import android.content.Context;

import java.lang.Thread.UncaughtExceptionHandler;

import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;

public interface InfraComponentsFactory {
    UncaughtExceptionHandler createExceptionHandler(Context currentActivityContext, ExceptionHandlerView view, ExceptionHandlerInteractor interactor);
}