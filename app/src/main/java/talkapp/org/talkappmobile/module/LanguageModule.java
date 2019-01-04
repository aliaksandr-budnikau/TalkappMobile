package talkapp.org.talkappmobile.module;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.impl.GrammarCheckServiceImpl;
import talkapp.org.talkappmobile.component.impl.LoggerBean;

/**
 * @author Budnikau Aliaksandr
 */
@Module
@EBean
public class LanguageModule {
    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;

    @Provides
    @Singleton
    public GrammarCheckService provideGrammarCheckService() {
        return new GrammarCheckServiceImpl(backendServerFactory.get(), logger);
    }
}