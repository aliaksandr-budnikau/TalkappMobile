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
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.ServiceFactory;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.dao.impl.PracticeWordSetExerciseDaoImpl;
import talkapp.org.talkappmobile.component.database.dao.impl.WordSetExperienceDaoImpl;
import talkapp.org.talkappmobile.component.database.dao.impl.local.TopicDaoImpl;
import talkapp.org.talkappmobile.component.database.dao.impl.local.WordSetDaoImpl;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.TopicMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.component.impl.LoggerBean;

@EBean(scope = EBean.Scope.Singleton)
public class ServiceFactoryBean implements ServiceFactory {

    private final ObjectMapper MAPPER = new ObjectMapper();
    @Bean(LoggerBean.class)
    Logger logger;
    @RootContext
    Context context;

    private DatabaseHelper databaseHelper;
    private PracticeWordSetExerciseDaoImpl exerciseDao;
    private WordSetExperienceDaoImpl experienceDao;
    private WordSetDao wordSetDao;
    private TopicDao topicDao;
    private PracticeWordSetExerciseServiceImpl practiceWordSetExerciseService;
    private WordSetExperienceServiceImpl wordSetExperienceService;
    private LocalDataService localDataService;

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

    @Override
    public LocalDataService getLocalDataService() {
        if (localDataService != null) {
            return localDataService;
        }
        localDataService = new LocalDataServiceImpl(provideWordSetDao(), provideTopicDao(), MAPPER, logger);
        return localDataService;
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

    private WordSetDao provideWordSetDao() {
        if (wordSetDao != null) {
            return wordSetDao;
        }
        try {
            wordSetDao = new WordSetDaoImpl(databaseHelper().getConnectionSource(), WordSetMapping.class);
            return wordSetDao;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private TopicDao provideTopicDao() {
        if (topicDao != null) {
            return topicDao;
        }
        try {
            topicDao = new TopicDaoImpl(databaseHelper().getConnectionSource(), TopicMapping.class);
            return topicDao;
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