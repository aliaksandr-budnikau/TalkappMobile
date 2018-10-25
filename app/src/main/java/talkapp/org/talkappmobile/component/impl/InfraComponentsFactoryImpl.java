package talkapp.org.talkappmobile.component.impl;

import android.content.Context;
import android.os.Handler;

import talkapp.org.talkappmobile.component.InfraComponentsFactory;

public class InfraComponentsFactoryImpl implements InfraComponentsFactory {
    @Override
    public Thread.UncaughtExceptionHandler createExceptionHandler(Context context, Handler uiEventHandler) {
        return new ExceptionHandler(context, uiEventHandler);
    }
}