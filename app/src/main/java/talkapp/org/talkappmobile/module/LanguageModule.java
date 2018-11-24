package talkapp.org.talkappmobile.module;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.impl.GrammarCheckServiceImpl;
import talkapp.org.talkappmobile.component.impl.LoggerBean;

/**
 * @author Budnikau Aliaksandr
 */
@Module
@EBean
public class LanguageModule {
    @Bean
    LoggerBean logger;

    @Provides
    @Singleton
    public GrammarCheckService provideGrammarCheckService(BackendServer server) {
        return new GrammarCheckServiceImpl(server, logger);
    }
}