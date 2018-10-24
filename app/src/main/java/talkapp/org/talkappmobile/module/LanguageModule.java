package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.impl.GrammarCheckServiceImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class LanguageModule {
    @Provides
    @Singleton
    public GrammarCheckService provideGrammarCheckService(BackendServer server, Logger logger) {
        return new GrammarCheckServiceImpl(server, logger);
    }
}