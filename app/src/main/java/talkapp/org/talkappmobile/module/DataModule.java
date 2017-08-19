package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.service.AuthSign;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class DataModule {

    @Provides
    @Singleton
    public AuthSign provideAuthSign() {
        return new AuthSign();
    }
}