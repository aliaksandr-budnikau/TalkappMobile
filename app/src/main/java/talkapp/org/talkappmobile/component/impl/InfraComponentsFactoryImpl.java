package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.activity.BaseActivity;
import talkapp.org.talkappmobile.component.InfraComponentsFactory;

public class InfraComponentsFactoryImpl implements InfraComponentsFactory {
    @Override
    public Thread.UncaughtExceptionHandler createExceptionHandler(BaseActivity applicationContext) {
        return new ExceptionHandler(applicationContext);
    }
}