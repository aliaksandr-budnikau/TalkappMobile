package talkapp.org.talkappmobile.module;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.activity.view.impl.ExceptionHandlerViewImpl;
import talkapp.org.talkappmobile.component.InfraComponentsFactory;
import talkapp.org.talkappmobile.component.impl.InfraComponentsFactoryImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class InfraModule {

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
    public ExceptionHandlerView provideExceptionHandlerView(Context context) {
        return new ExceptionHandlerViewImpl(context);
    }
}