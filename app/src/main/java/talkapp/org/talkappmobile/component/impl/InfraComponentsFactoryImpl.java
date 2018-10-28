package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.component.InfraComponentsFactory;

public class InfraComponentsFactoryImpl implements InfraComponentsFactory {
    @Override
    public Thread.UncaughtExceptionHandler createExceptionHandler(ExceptionHandlerView view, ExceptionHandlerInteractor interactor) {
        return new ExceptionHandler(view, interactor);
    }
}