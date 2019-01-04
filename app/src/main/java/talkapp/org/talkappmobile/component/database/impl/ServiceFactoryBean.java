package talkapp.org.talkappmobile.component.database.impl;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.sql.SQLException;

import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.ServiceFactory;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.dao.impl.PracticeWordSetExerciseDaoImpl;
import talkapp.org.talkappmobile.component.database.dao.impl.WordSetExperienceDaoImpl;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.component.impl.LoggerBean;

@EBean(scope = EBean.Scope.Singleton)
public class ServiceFactoryBean implements ServiceFactory {

    @Bean(LoggerBean.class)
    Logger logger;
    @RootContext
    Context context;

    private DatabaseHelper databaseHelper;
    private PracticeWordSetExerciseDaoImpl exerciseDao;
    private WordSetExperienceDaoImpl experienceDao;
    private PracticeWordSetExerciseServiceImpl practiceWordSetExerciseService;
    private WordSetExperienceServiceImpl wordSetExperienceService;

    @Override
    public WordSetExperienceService getWordSetExperienceRepository() {
        if (wordSetExperienceService != null) {
            return wordSetExperienceService;
        }
        wordSetExperienceService = new WordSetExperienceServiceImpl(provideWordSetExperienceDao(), logger);
        return wordSetExperienceService;
    }

    @Override
    public PracticeWordSetExerciseService getPracticeWordSetExerciseRepository() {
        if (practiceWordSetExerciseService != null) {
            return practiceWordSetExerciseService;
        }
        practiceWordSetExerciseService = new PracticeWordSetExerciseServiceImpl(
                providePracticeWordSetExerciseDao(),
                provideWordSetExperienceDao(),
                new ObjectMapper()
        );
        return practiceWordSetExerciseService;
    }

    private PracticeWordSetExerciseDao providePracticeWordSetExerciseDao() {
        if (exerciseDao != null) {
            return exerciseDao;
        }
        try {
            exerciseDao = new PracticeWordSetExerciseDaoImpl(databaseHelper().getConnectionSource(), PracticeWordSetExerciseMapping.class);
            return exerciseDao;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private WordSetExperienceDao provideWordSetExperienceDao() {
        if (experienceDao != null) {
            return experienceDao;
        }
        try {
            experienceDao = new WordSetExperienceDaoImpl(databaseHelper().getConnectionSource(), WordSetExperienceMapping.class);
            return experienceDao;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private DatabaseHelper databaseHelper() {
        if (databaseHelper != null) {
            return databaseHelper;
        }
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        return databaseHelper;
    }
}