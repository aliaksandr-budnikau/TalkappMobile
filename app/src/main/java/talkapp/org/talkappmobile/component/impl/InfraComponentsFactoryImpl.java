package talkapp.org.talkappmobile.component.impl;

import android.content.Context;

import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.component.InfraComponentsFactory;

public class InfraComponentsFactoryImpl implements InfraComponentsFactory {
    @Override
    public Thread.UncaughtExceptionHandler createExceptionHandler(Context currentActivityContext, ExceptionHandlerView view, ExceptionHandlerInteractor interactor) {
        return new ExceptionHandler(currentActivityContext, view, interactor);
    }
}