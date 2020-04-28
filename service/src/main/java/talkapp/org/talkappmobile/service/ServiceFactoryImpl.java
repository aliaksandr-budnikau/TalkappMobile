package talkapp.org.talkappmobile.service;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryProvider;
import talkapp.org.talkappmobile.repository.WordTranslationRepository;

public class ServiceFactoryImpl implements ServiceFactory {

    public static final int TIMEOUT = 50;
    public static final String SERVER_URL = "http://192.168.0.101:8080";
    public static final String GIT_HUB_URL = "https://raw.githubusercontent.com";
    private final ObjectMapper MAPPER = new ObjectMapper();
    private final RepositoryFactory repositoryFactory;
    private WordRepetitionProgressServiceImpl practiceWordSetExerciseService;
    private WordSetService wordSetService;
    private UserExpService userExpService;
    private TopicService topicService;
    private WordTranslationService wordTranslationService;
    private CurrentPracticeStateServiceImpl currentPracticeStateService;
    private SentenceService sentenceService;
    private RequestExecutor requestExecutor;
    private Retrofit retrofit;
    private Retrofit gitHubRetrofit;
    private DataServer backendServer;
    private GitHubRestClient gitHubRestClient;
    private EqualityScorer equalityScorer;
    private Logger logger;
    private TextUtils textUtils;

    public ServiceFactoryImpl(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    public ServiceFactoryImpl(Context context) {
        this.repositoryFactory = RepositoryFactoryProvider.get(context);
    }

    private RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
    }

    private RequestExecutor getRequestExecutor() {
        if (requestExecutor != null) {
            return requestExecutor;
        }
        requestExecutor = new RequestExecutor();
        return requestExecutor;
    }

    public void setRequestExecutor(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    @Override
    public TextUtils getTextUtils() {
        if (textUtils != null) {
            return textUtils;
        }
        textUtils = new TextUtilsImpl();
        return textUtils;
    }

    @Override
    public EqualityScorer getEqualityScorer() {
        if (equalityScorer != null) {
            return equalityScorer;
        }
        equalityScorer = new EqualityScorerImpl();
        return equalityScorer;
    }

    @Override
    public Logger getLogger() {
        if (logger != null) {
            return logger;
        }
        logger = new LoggerImpl();
        return logger;
    }

    @Override
    public WordSetService getWordSetExperienceRepository() {
        if (wordSetService != null) {
            return wordSetService;
        }
        WordSetServiceImpl wordSetService = new WordSetServiceImpl(getDataServer(), getRepositoryFactory().getWordSetRepository());
        this.wordSetService = new CachedWordSetServiceDecorator(getRepositoryFactory().getWordSetRepository(), wordSetService);
        return this.wordSetService;
    }

    @Override
    public WordRepetitionProgressService getWordRepetitionProgressService() {
        if (practiceWordSetExerciseService != null) {
            return practiceWordSetExerciseService;
        }
        practiceWordSetExerciseService = new WordRepetitionProgressServiceImpl(
                getRepositoryFactory().getWordRepetitionProgressRepository(),
                getRepositoryFactory().getWordSetRepository(),
                getRepositoryFactory().getSentenceRepository()
        );
        return practiceWordSetExerciseService;
    }

    @Override
    public UserExpService getUserExpService() {
        if (userExpService != null) {
            return userExpService;
        }
        userExpService = new UserExpServiceImpl(getRepositoryFactory().getExpAuditRepository());
        return userExpService;
    }

    @Override
    public WordTranslationService getWordTranslationService() {
        if (wordTranslationService != null) {
            return wordTranslationService;
        }
        wordTranslationService = new WordTranslationServiceImpl(getDataServer(), getRepositoryFactory().getWordTranslationRepository(), getRepositoryFactory().getWordSetRepository());
        return wordTranslationService;
    }

    @Override
    public TopicService getTopicService() {
        if (topicService != null) {
            return topicService;
        }
        topicService = new TopicServiceImpl(getRepositoryFactory().getTopicRepository(), getDataServer());
        return topicService;
    }

    private ObjectMapper getMapper() {
        return MAPPER;
    }

    @Override
    public CurrentPracticeStateService getCurrentPracticeStateService() {
        if (currentPracticeStateService != null) {
            return currentPracticeStateService;
        }
        currentPracticeStateService = new CurrentPracticeStateServiceImpl(getRepositoryFactory().getWordSetRepository());
        return currentPracticeStateService;
    }

    @Override
    public SentenceService getSentenceService() {
        if (sentenceService != null) {
            return sentenceService;
        }
        DataServer dataServer = getDataServer();
        sentenceService = new SentenceServiceImpl(dataServer, getRepositoryFactory().getSentenceRepository());
        return sentenceService;
    }

    @Override
    public RefereeService getRefereeService() {
        return new RefereeServiceImpl(getEqualityScorer());
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
        if (gitHubRestClient != null) {
            return gitHubRestClient;
        }
        gitHubRestClient = gitHubRetrofit().create(GitHubRestClient.class);
        return gitHubRestClient;
    }

    @Override
    public SentenceProvider getSentenceProvider() {
        SentenceProvider sentenceProvider = new SentenceProviderImpl(getRepositoryFactory().getWordSetRepository(), getRepositoryFactory().getWordRepetitionProgressRepository(), getRepositoryFactory().getSentenceRepository(), getMapper());
        ServerSentenceProviderDecorator serverSentenceProviderDecorator = new ServerSentenceProviderDecorator(sentenceProvider, getDataServer());
        CachedSentenceProviderDecorator cachedSentenceProviderDecorator = new CachedSentenceProviderDecorator(serverSentenceProviderDecorator, getRepositoryFactory().getSentenceRepository());
        WordTranslationRepository wordTranslationRepository = getRepositoryFactory().getWordTranslationRepository();
        WordTranslationSentenceProviderDecorator translationSentenceProviderDecorator = new WordTranslationSentenceProviderDecorator(cachedSentenceProviderDecorator, wordTranslationRepository);
        return new WordProgressSentenceProviderDecorator(translationSentenceProviderDecorator, getRepositoryFactory().getWordSetRepository(), getRepositoryFactory().getWordRepetitionProgressRepository());
    }

    public void setGitHubRestClient(GitHubRestClient gitHubRestClient) {
        this.gitHubRestClient = gitHubRestClient;
    }
}