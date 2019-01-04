package talkapp.org.talkappmobile.module;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

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
import talkapp.org.talkappmobile.component.impl.LoggerBean;

/**
 * @author Budnikau Aliaksandr
 */
@Module
@EBean
public class DatabaseModule {
    @Bean(LoggerBean.class)
    Logger logger;
    @RootContext
    Context context;

    private PracticeWordSetExerciseDao providePracticeWordSetExerciseDao(DatabaseHelper databaseHelper) {
        try {
            return new PracticeWordSetExerciseDaoImpl(databaseHelper.getConnectionSource(), PracticeWordSetExerciseMapping.class);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private WordSetExperienceDao provideWordSetExperienceDao(DatabaseHelper databaseHelper) {
        try {
            return new WordSetExperienceDaoImpl(databaseHelper.getConnectionSource(), WordSetExperienceMapping.class);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Provides
    @Singleton
    public PracticeWordSetExerciseService providePracticeWordSetExerciseRepository() {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        PracticeWordSetExerciseDao exerciseDao = providePracticeWordSetExerciseDao(databaseHelper);
        WordSetExperienceDao experienceDao = provideWordSetExperienceDao(databaseHelper);
        return new PracticeWordSetExerciseServiceImpl(exerciseDao, experienceDao, new ObjectMapper());
    }

    @Provides
    @Singleton
    public WordSetExperienceService provideWordSetExperienceRepository() {
        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        WordSetExperienceDao experienceDao = provideWordSetExperienceDao(databaseHelper);
        return new WordSetExperienceServiceImpl(experienceDao, logger);
    }
}