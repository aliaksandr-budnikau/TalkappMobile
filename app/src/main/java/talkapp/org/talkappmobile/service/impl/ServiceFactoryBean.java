package talkapp.org.talkappmobile.service.impl;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.dao.impl.ExpAuditDaoImpl;
import talkapp.org.talkappmobile.dao.impl.NewWordSetDraftDaoImpl;
import talkapp.org.talkappmobile.dao.impl.SentenceDaoImpl;
import talkapp.org.talkappmobile.dao.impl.TopicDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordRepetitionProgressDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordSetDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordTranslationDaoImpl;
import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.mappings.NewWordSetDraftMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.TopicMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.service.CachedWordSetServiceDecorator;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.GitHubRestClient;
import talkapp.org.talkappmobile.service.MigrationService;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.SentenceRestClient;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.TopicService;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetRepository;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.mapper.ExpAuditMapper;

@EBean(scope = EBean.Scope.Singleton)
public class ServiceFactoryBean implements ServiceFactory {

    public static final int TIMEOUT = 50;
    public static final String SERVER_URL = "http://192.168.0.101:8080";
    public static final String GIT_HUB_URL = "https://raw.githubusercontent.com";
    private final ObjectMapper MAPPER = new ObjectMapper();
    private Context context;

    private DatabaseHelper databaseHelper;
    private WordRepetitionProgressDaoImpl exerciseDao;
    private WordSetDao wordSetDao;
    private TopicDao topicDao;
    private SentenceDao sentenceDao;
    private WordTranslationDao wordTranslationDao;
    private WordRepetitionProgressServiceImpl practiceWordSetExerciseService;
    private WordSetService wordSetService;
    private UserExpService userExpService;
    private ExpAuditMapper expAuditMapper;
    private TopicService topicService;
    private WordTranslationService wordTranslationService;
    private ExpAuditDao expAuditDao;
    private NewWordSetDraftDao newWordSetDraftDao;
    private MigrationService migrationService;
    private CurrentPracticeStateServiceImpl currentPracticeStateService;
    private SentenceService sentenceService;
    private RequestExecutor requestExecutor;
    private Retrofit retrofit;
    private Retrofit gitHubRetrofit;
    private DataServer backendServer;
    private WordSetRepository wordSetRepository;

    @Override
    public RequestExecutor getRequestExecutor() {
        if (requestExecutor != null) {
            return requestExecutor;
        }
        requestExecutor = new RequestExecutor();
        return requestExecutor;
    }

    @Override
    public WordSetService getWordSetExperienceRepository() {
        if (wordSetService != null) {
            return wordSetService;
        }
        WordSetServiceImpl wordSetService = new WordSetServiceImpl(getDataServer(), getWordSetRepository());
        this.wordSetService = new CachedWordSetServiceDecorator(getWordSetRepository(), wordSetService);
        return this.wordSetService;
    }

    @Override
    public MigrationService getMigrationService() {
        if (migrationService != null) {
            return migrationService;
        }
        migrationService = new MigrationServiceImpl(providePracticeWordSetExerciseDao(), provideWordSetDao(), provideSentenceDao(), MAPPER);
        return migrationService;
    }

    @Override
    public WordRepetitionProgressService getPracticeWordSetExerciseRepository() {
        if (practiceWordSetExerciseService != null) {
            return practiceWordSetExerciseService;
        }
        practiceWordSetExerciseService = new WordRepetitionProgressServiceImpl(
                providePracticeWordSetExerciseDao(),
                getWordSetRepository(),
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
        userExpService = new UserExpServiceImpl(provideExpAuditDao(), getExpAuditMapper());
        return userExpService;
    }

    @Override
    public WordTranslationService getWordTranslationService() {
        if (wordTranslationService != null) {
            return wordTranslationService;
        }
        wordTranslationService = new WordTranslationServiceImpl(getDataServer(), provideWordTranslationDao(), getWordSetRepository(), getMapper());
        return wordTranslationService;
    }

    @Override
    public ExpAuditMapper getExpAuditMapper() {
        if (expAuditMapper != null) {
            return expAuditMapper;
        }
        expAuditMapper = new ExpAuditMapper();
        return expAuditMapper;
    }

    @Override
    public TopicService getTopicService() {
        if (topicService != null) {
            return topicService;
        }
        topicService = new TopicServiceImpl(provideTopicDao(), getDataServer());
        return topicService;
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

    private NewWordSetDraftDao provideNewWordSetDraftDao() {
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

    protected DatabaseHelper databaseHelper() {
        if (databaseHelper != null) {
            return databaseHelper;
        }
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        databaseHelper.setMigrationService(getMigrationService());
        return databaseHelper;
    }

    @Override
    public ObjectMapper getMapper() {
        return MAPPER;
    }

    @Override
    public CurrentPracticeStateService getCurrentPracticeStateService() {
        if (currentPracticeStateService != null) {
            return currentPracticeStateService;
        }
        currentPracticeStateService = new CurrentPracticeStateServiceImpl(getWordSetRepository());
        return currentPracticeStateService;
    }

    @Override
    public SentenceService getSentenceService(DataServer server) {
        if (sentenceService != null) {
            return sentenceService;
        }
        DataServer dataServer = server == null ? getDataServer() : server;
        sentenceService = new SentenceServiceImpl(dataServer,
                provideSentenceDao(), getMapper());
        return sentenceService;
    }

    private Retrofit retrofit() {
        if (retrofit != null) {
            return retrofit;
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .client(okHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(jacksonConverterFactory())
                .build();
        return retrofit;
    }

    private Retrofit gitHubRetrofit() {
        if (gitHubRetrofit != null) {
            return gitHubRetrofit;
        }
        gitHubRetrofit = new Retrofit.Builder()
                .baseUrl(GIT_HUB_URL)
                .client(okHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(jacksonConverterFactory())
                .build();
        return gitHubRetrofit;
    }

    private OkHttpClient okHttpClient() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS).build();
    }

    private JacksonConverterFactory jacksonConverterFactory() {
        return JacksonConverterFactory.create(getMapper());
    }

    @Override
    public synchronized DataServer getDataServer() {
        if (backendServer != null) {
            return backendServer;
        }
        backendServer = new DataServerImpl(
                sentenceRestClient(),
                gitHubRestClient(),
                getRequestExecutor()
        );
        return backendServer;
    }

    private SentenceRestClient sentenceRestClient() {
        return retrofit().create(SentenceRestClient.class);
    }

    protected GitHubRestClient gitHubRestClient() {
        return gitHubRetrofit().create(GitHubRestClient.class);
    }

    @Override
    public SentenceProvider getSentenceProvider() {
        SentenceProvider sentenceProvider = new SentenceProviderImpl(getWordSetRepository(), providePracticeWordSetExerciseDao(), provideSentenceDao(), getMapper());
        ServerSentenceProviderDecorator serverSentenceProviderDecorator = new ServerSentenceProviderDecorator(sentenceProvider, getDataServer());
        CachedSentenceProviderDecorator cachedSentenceProviderDecorator = new CachedSentenceProviderDecorator(serverSentenceProviderDecorator, provideSentenceDao(), getMapper());
        WordTranslationSentenceProviderDecorator translationSentenceProviderDecorator = new WordTranslationSentenceProviderDecorator(cachedSentenceProviderDecorator, provideWordTranslationDao(), getMapper());
        return new WordProgressSentenceProviderDecorator(translationSentenceProviderDecorator, getWordSetRepository(), providePracticeWordSetExerciseDao(), getMapper());
    }

    @Override
    public WordSetRepository getWordSetRepository() {
        if (wordSetRepository != null) {
            return wordSetRepository;
        }
        wordSetRepository = new WordSetRepositoryImpl(provideWordSetDao(), provideNewWordSetDraftDao(), getMapper());
        return wordSetRepository;
    }

    @RootContext
    public void setContext(Context context) {
        this.context = context;
    }

    @Deprecated
    public WordRepetitionProgressDaoImpl getExerciseDao() {
        providePracticeWordSetExerciseDao();
        return exerciseDao;
    }
}