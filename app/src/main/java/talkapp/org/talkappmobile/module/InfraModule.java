package talkapp.org.talkappmobile.module;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.activity.view.impl.ExceptionHandlerViewBean;
import talkapp.org.talkappmobile.component.InfraComponentsFactory;
import talkapp.org.talkappmobile.component.impl.InfraComponentsFactoryImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
@EBean
public class InfraModule {
    @Bean(ExceptionHandlerViewBean.class)
    ExceptionHandlerView exceptionHandlerView;

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
    public ExceptionHandlerView provideExceptionHandlerView() {
        return exceptionHandlerView;
    }
}