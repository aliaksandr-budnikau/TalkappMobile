package talkapp.org.talkappmobile.module;

import android.os.Handler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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
}