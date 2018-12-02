package talkapp.org.talkappmobile.module;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.sql.SQLException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.dao.impl.PracticeWordSetExerciseDaoImpl;
import talkapp.org.talkappmobile.component.database.dao.impl.WordSetExperienceDaoImpl;
import talkapp.org.talkappmobile.component.database.impl.PracticeWordSetExerciseServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.WordSetExperienceServiceImpl;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.component.impl.LoggerImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
@EBean
public class DatabaseModule {
    @Bean(LoggerImpl.class)
    Logger logger;

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
    public PracticeWordSetExerciseService providePracticeWordSetExerciseRepository(PracticeWordSetExerciseDao exerciseDao, WordSetExperienceDao experienceDao, ObjectMapper mapper) {
        return new PracticeWordSetExerciseServiceImpl(exerciseDao, experienceDao, mapper);
    }

    @Provides
    @Singleton
    public WordSetExperienceService provideWordSetExperienceRepository(WordSetExperienceDao experienceDao) {
        return new WordSetExperienceServiceImpl(experienceDao, logger);
    }
}