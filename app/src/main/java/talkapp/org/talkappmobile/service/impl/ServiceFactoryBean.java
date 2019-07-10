package talkapp.org.talkappmobile.service.impl;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.dao.impl.ExpAuditDaoImpl;
import talkapp.org.talkappmobile.dao.impl.SentenceDaoImpl;
import talkapp.org.talkappmobile.dao.impl.TopicDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordRepetitionProgressDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordSetDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordTranslationDaoImpl;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.TopicMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.service.LocalDataService;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import java.sql.SQLException;

@EBean(scope = EBean.Scope.Singleton)
public class ServiceFactoryBean implements ServiceFactory {

    private final ObjectMapper MAPPER = new ObjectMapper();
    @Bean(LoggerBean.class)
    Logger logger;
    @RootContext
    Context context;

    private DatabaseHelper databaseHelper;
    private WordRepetitionProgressDaoImpl exerciseDao;
    private WordSetDao wordSetDao;
    private TopicDao topicDao;
    private SentenceDao sentenceDao;
    private WordTranslationDao wordTranslationDao;
    private WordRepetitionProgressServiceImpl practiceWordSetExerciseService;
    private WordSetServiceImpl wordSetExperienceService;
    private UserExpService userExpService;
    private WordSetMapper wordSetMapper;
    private LocalDataService localDataService;
    private ExpAuditDao expAuditDao;
    private WordSetExperienceUtils experienceUtils;

    @Override
    public WordSetService getWordSetExperienceRepository() {
        if (wordSetExperienceService != null) {
            return wordSetExperienceService;
        }
        wordSetExperienceService = new WordSetServiceImpl(provideWordSetDao(), provideExperienceUtils(), getWordSetMapper());
        return wordSetExperienceService;
    }

    @Override
    public WordRepetitionProgressService getPracticeWordSetExerciseRepository() {
        if (practiceWordSetExerciseService != null) {
            return practiceWordSetExerciseService;
        }
        practiceWordSetExerciseService = new WordRepetitionProgressServiceImpl(
                providePracticeWordSetExerciseDao(),
                provideWordSetDao(),
                provideSentenceDao(),
                new ObjectMapper()
        );
        return practiceWordSetExerciseService;
    }

    @Override
    public UserExpService getUserExpService() {
        if (userExpService != null) {
            return userExpService;
        }
        userExpService = new UserExpServiceImpl(provideExpAuditDao());
        return userExpService;
    }

    @Override
    public WordSetMapper getWordSetMapper() {
        if (wordSetMapper != null) {
            return wordSetMapper;
        }
        wordSetMapper = new WordSetMapper(MAPPER);
        return wordSetMapper;
    }

    @Override
    public LocalDataService getLocalDataService() {
        if (localDataService != null) {
            return localDataService;
        }
        localDataService = new LocalDataServiceImpl(provideWordSetDao(), provideTopicDao(), provideSentenceDao(), provideWordTranslationDao(), MAPPER, logger);
        return localDataService;
    }

    private WordRepetitionProgressDao providePracticeWordSetExerciseDao() {
        if (exerciseDao != null) {
            return exerciseDao;
        }
        try {
            exerciseDao = new WordRepetitionProgressDaoImpl(databaseHelper().getConnectionSource(), WordRepetitionProgressMapping.class);
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

    private WordSetExperienceUtils provideExperienceUtils() {
        if (experienceUtils != null) {
            return experienceUtils;
        }
        experienceUtils = new WordSetExperienceUtilsImpl();
        return experienceUtils;
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

    private SentenceDao provideSentenceDao() {
        if (sentenceDao != null) {
            return sentenceDao;
        }
        try {
            sentenceDao = new SentenceDaoImpl(databaseHelper().getConnectionSource(), SentenceMapping.class);
            return sentenceDao;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private WordTranslationDao provideWordTranslationDao() {
        if (wordTranslationDao != null) {
            return wordTranslationDao;
        }
        try {
            wordTranslationDao = new WordTranslationDaoImpl(databaseHelper().getConnectionSource(), WordTranslationMapping.class);
            return wordTranslationDao;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private ExpAuditDao provideExpAuditDao() {
        if (expAuditDao != null) {
            return expAuditDao;
        }
        try {
            expAuditDao = new ExpAuditDaoImpl(databaseHelper().getConnectionSource(), ExpAuditMapping.class);
            return expAuditDao;
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