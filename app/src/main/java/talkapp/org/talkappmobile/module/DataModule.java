package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.SaveSharedPreference;
import talkapp.org.talkappmobile.component.Word2SentenceCache;
import talkapp.org.talkappmobile.component.impl.SaveSharedPreferenceImpl;
import talkapp.org.talkappmobile.component.impl.Word2SentenceCacheImpl;

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

    @Provides
    @Singleton
    public SaveSharedPreference provideSaveSharedPreference() {
        return new SaveSharedPreferenceImpl();
    }

    @Provides
    @Singleton
    public Word2SentenceCache provideWord2SentenceCache() {
        return new Word2SentenceCacheImpl();
    }
}