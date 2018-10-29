package talkapp.org.talkappmobile.module;

import android.content.Context;
import android.os.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.activity.view.impl.ExceptionHandlerViewImpl;
import talkapp.org.talkappmobile.component.InfraComponentsFactory;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.impl.InfraComponentsFactoryImpl;
import talkapp.org.talkappmobile.component.impl.LoggerImpl;

import static android.os.Looper.getMainLooper;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class InfraModule {

    @Provides
    @Singleton
    public Logger provideLogger() {
        return new LoggerImpl();
    }

    @Provides
    @Singleton
    public Handler provideHandler() {
        return new Handler(getMainLooper());
    }

    @Provides
    @Singleton
    public InfraComponentsFactory provideInfraComponentsFactory() {
        return new InfraComponentsFactoryImpl();
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    public ExceptionHandlerView provideExceptionHandlerView(Context context, Handler uiEventHandler, Logger logger) {
        return new ExceptionHandlerViewImpl(context, uiEventHandler, logger);
    }
}