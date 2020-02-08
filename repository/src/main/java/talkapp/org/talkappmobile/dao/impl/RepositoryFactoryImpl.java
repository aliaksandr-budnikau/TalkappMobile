package talkapp.org.talkappmobile.dao.impl;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.dao.RepositoryFactory;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.mappings.NewWordSetDraftMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.TopicMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.repository.ExpAuditRepository;
import talkapp.org.talkappmobile.repository.MigrationService;
import talkapp.org.talkappmobile.repository.SentenceRepository;
import talkapp.org.talkappmobile.repository.TopicRepository;
import talkapp.org.talkappmobile.repository.WordRepetitionProgressRepository;
import talkapp.org.talkappmobile.repository.WordSetRepository;
import talkapp.org.talkappmobile.repository.WordTranslationRepository;
import talkapp.org.talkappmobile.repository.impl.ExpAuditRepositoryImpl;
import talkapp.org.talkappmobile.repository.impl.MigrationServiceImpl;
import talkapp.org.talkappmobile.repository.impl.SentenceRepositoryImpl;
import talkapp.org.talkappmobile.repository.impl.TopicRepositoryImpl;
import talkapp.org.talkappmobile.repository.impl.WordRepetitionProgressRepositoryImpl;
import talkapp.org.talkappmobile.repository.impl.WordSetRepositoryImpl;
import talkapp.org.talkappmobile.repository.impl.WordTranslationRepositoryImpl;

public class RepositoryFactoryImpl implements RepositoryFactory {

    private final ObjectMapper MAPPER = new ObjectMapper();
    private WordRepetitionProgressDao wordRepetitionProgressDao;
    private WordSetDao wordSetDao;
    private TopicDao topicDao;
    private SentenceDao sentenceDao;
    private ExpAuditDao expAuditDao;
    private NewWordSetDraftDao newWordSetDraftDao;
    private WordTranslationDao wordTranslationDao;
    private WordSetRepository wordSetRepository;
    private ExpAuditRepository expAuditRepository;
    private SentenceRepository sentenceRepository;
    private WordTranslationRepository wordTranslationRepository;
    private WordRepetitionProgressRepository wordRepetitionProgressRepository;
    @Deprecated
    private MigrationService migrationService;

    private Context context;
    private DatabaseHelper databaseHelper;

    public RepositoryFactoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public WordRepetitionProgressRepository getWordRepetitionProgressRepository() {
        if (wordRepetitionProgressRepository != null) {
            return wordRepetitionProgressRepository;
        }
        wordRepetitionProgressRepository = new WordRepetitionProgressRepositoryImpl(getPracticeWordSetExerciseDao(), getMapper());
        return wordRepetitionProgressRepository;
    }

    @Override
    public WordSetRepository getWordSetRepository() {
        if (wordSetRepository != null) {
            return wordSetRepository;
        }
        wordSetRepository = new WordSetRepositoryImpl(getWordSetDao(), getNewWordSetDraftDao(), getMapper());
        return wordSetRepository;
    }

    @Override
    public WordTranslationRepository getWordTranslationRepository() {
        if (wordTranslationRepository != null) {
            return wordTranslationRepository;
        }
        wordTranslationRepository = new WordTranslationRepositoryImpl(getWordTranslationDao());
        return wordTranslationRepository;
    }

    @Override
    public ExpAuditRepository getExpAuditRepository() {
        if (expAuditRepository != null) {
            return expAuditRepository;
        }
        expAuditRepository = new ExpAuditRepositoryImpl(getExpAuditDao());
        return expAuditRepository;
    }

    @Override
    public SentenceRepository getSentenceRepository() {
        if (sentenceRepository != null) {
            return sentenceRepository;
        }
        sentenceRepository = new SentenceRepositoryImpl(getSentenceDao(), getMapper());
        return sentenceRepository;
    }

    protected DatabaseHelper databaseHelper() {
        if (databaseHelper != null) {
            return databaseHelper;
        }
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        databaseHelper.setMigrationService(getMigrationService());
        return databaseHelper;
    }

    @Deprecated
    @Override
    public MigrationService getMigrationService() {
        if (migrationService != null) {
            return migrationService;
        }
        migrationService = new MigrationServiceImpl(getPracticeWordSetExerciseDao(), getWordSetDao(), getSentenceDao(), MAPPER);
        return migrationService;
    }

    @Override
    public TopicRepository getTopicRepository() {
        return new TopicRepositoryImpl(getTopicDao());
    }

    private WordTranslationDao getWordTranslationDao() {
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

    private WordRepetitionProgressDao getPracticeWordSetExerciseDao() {
        if (wordRepetitionProgressDao != null) {
            return wordRepetitionProgressDao;
        }
        try {
            wordRepetitionProgressDao = new WordRepetitionProgressDaoImpl(databaseHelper().getConnectionSource(), WordRepetitionProgressMapping.class);
            return wordRepetitionProgressDao;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private WordSetDao getWordSetDao() {
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

    private ExpAuditDao getExpAuditDao() {
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

    private TopicDao getTopicDao() {
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

    private SentenceDao getSentenceDao() {
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

    private NewWordSetDraftDao getNewWordSetDraftDao() {
        if (newWordSetDraftDao != null) {
            return newWordSetDraftDao;
        }
        try {
            newWordSetDraftDao = new NewWordSetDraftDaoImpl(databaseHelper().getConnectionSource(), NewWordSetDraftMapping.class);
            return newWordSetDraftDao;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private ObjectMapper getMapper() {
        return MAPPER;
    }

}

