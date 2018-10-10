package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.PracticeWordSetExerciseTempRepository;
import talkapp.org.talkappmobile.component.SaveSharedPreference;
import talkapp.org.talkappmobile.component.impl.PracticeWordSetExerciseTempRepositoryImpl;
import talkapp.org.talkappmobile.component.impl.SaveSharedPreferenceImpl;

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
    public PracticeWordSetExerciseTempRepository provideWord2SentenceCache() {
        return new PracticeWordSetExerciseTempRepositoryImpl();
    }
}