package talkapp.org.talkappmobile.module;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.component.WordSetExperienceRepository;
import talkapp.org.talkappmobile.component.impl.PracticeWordSetExerciseRepositoryImpl;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceRepositoryImpl;
import talkapp.org.talkappmobile.db.DatabaseHelper;
import talkapp.org.talkappmobile.db.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.db.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.db.dao.impl.PracticeWordSetExerciseDaoImpl;
import talkapp.org.talkappmobile.db.dao.impl.WordSetExperienceDaoImpl;
import talkapp.org.talkappmobile.db.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.db.mappings.WordSetExperienceMapping;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public DatabaseHelper provideDatabaseHelper(Context context) {
        return OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    @Provides
    @Singleton
    public PracticeWordSetExerciseDao providePracticeWordSetExerciseDao(DatabaseHelper databaseHelper) {
        try {
            return new PracticeWordSetExerciseDaoImpl(databaseHelper.getConnectionSource(), PracticeWordSetExerciseMapping.class);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Provides
    @Singleton
    public WordSetExperienceDao provideWordSetExperienceDao(DatabaseHelper databaseHelper) {
        try {
            return new WordSetExperienceDaoImpl(databaseHelper.getConnectionSource(), WordSetExperienceMapping.class);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Provides
    @Singleton
    public PracticeWordSetExerciseRepository providePracticeWordSetExerciseRepository(PracticeWordSetExerciseDao exerciseDao, ObjectMapper mapper) {
        return new PracticeWordSetExerciseRepositoryImpl(exerciseDao, mapper);
    }

    @Provides
    @Singleton
    public WordSetExperienceRepository provideWordSetExperienceRepository(WordSetExperienceDao experienceDao) {
        return new WordSetExperienceRepositoryImpl(experienceDao);
    }
}