package talkapp.org.talkappmobile.component;

import android.content.Context;
import android.os.Handler;

import java.lang.Thread.UncaughtExceptionHandler;

public interface InfraComponentsFactory {
    UncaughtExceptionHandler createExceptionHandler(Context context, Handler uiEventHandler);
}