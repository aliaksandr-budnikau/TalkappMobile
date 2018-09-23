package talkapp.org.talkappmobile.component;

import java.lang.Thread.UncaughtExceptionHandler;

import talkapp.org.talkappmobile.activity.BaseActivity;

public interface InfraComponentsFactory {
    UncaughtExceptionHandler createExceptionHandler(BaseActivity applicationContext);
}