package talkapp.org.talkappmobile.module;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.component.SaveSharedPreference;
import talkapp.org.talkappmobile.component.impl.PracticeWordSetExerciseRepositoryImpl;
import talkapp.org.talkappmobile.component.impl.SaveSharedPreferenceImpl;
import talkapp.org.talkappmobile.db.dao.PracticeWordSetExerciseDao;

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
    public PracticeWordSetExerciseRepository provideWord2SentenceCache(PracticeWordSetExerciseDao exerciseDao, ObjectMapper mapper) {
        return new PracticeWordSetExerciseRepositoryImpl(exerciseDao, mapper);
    }
}