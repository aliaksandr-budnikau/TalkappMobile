package talkapp.org.talkappmobile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import talkapp.org.talkappmobile.repository.ExpAuditRepository;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.SentenceRepository;
import talkapp.org.talkappmobile.repository.TopicRepository;
import talkapp.org.talkappmobile.repository.WordRepetitionProgressRepository;
import talkapp.org.talkappmobile.repository.WordSetRepository;
import talkapp.org.talkappmobile.repository.WordTranslationRepository;
import talkapp.org.talkappmobile.service.CachedSentenceProviderDecorator;
import talkapp.org.talkappmobile.service.CachedWordSetServiceDecorator;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.GitHubRestClient;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.SentenceProviderImpl;
import talkapp.org.talkappmobile.service.SentenceRestClient;
import talkapp.org.talkappmobile.service.ServerSentenceProviderDecorator;
import talkapp.org.talkappmobile.service.WordProgressSentenceProviderDecorator;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordSetServiceImpl;
import talkapp.org.talkappmobile.service.WordTranslationSentenceProviderDecorator;

@Module
public class ServiceModule {

    public static final int TIMEOUT = 50;
    public static final String SERVER_URL = "http://192.168.0.101:8080";
    public static final String GIT_HUB_URL = "https://raw.githubusercontent.com";
    private final RepositoryFactory repositoryFactory;

    public ServiceModule(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    @Provides
    @Singleton
    public RepositoryFactory repositoryFactory() {
        return repositoryFactory;
    }

    @Provides
    @Singleton
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    public Retrofit gitHubRetrofit(OkHttpClient okHttpClient, JacksonConverterFactory jacksonConverterFactory, ScalarsConverterFactory scalarsConverterFactory) {
        return new Retrofit.Builder()
                .baseUrl(GIT_HUB_URL)
                .client(okHttpClient)
                .addConverterFactory(scalarsConverterFactory)
                .addConverterFactory(jacksonConverterFactory)
                .build();
    }


    @Provides
    @Singleton
    public JacksonConverterFactory jacksonConverterFactory(ObjectMapper mapper) {
        return JacksonConverterFactory.create(mapper);
    }

    @Provides
    @Singleton
    public ScalarsConverterFactory scalarsConverterFactory() {
        return ScalarsConverterFactory.create();
    }

    @Provides
    @Singleton
    public OkHttpClient okHttpClient() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS).build();
    }

    @Provides
    @Singleton
    public GitHubRestClient gitHubRestClient(Retrofit retrofit) {
        return retrofit.create(GitHubRestClient.class);
    }

    @Provides
    @Singleton
    public SentenceRestClient sentenceRestClient(Retrofit retrofit) {
        return retrofit.create(SentenceRestClient.class);
    }

    @Provides
    @Singleton
    public WordSetRepository wordSetRepository(RepositoryFactory repositoryFactory) {
        return repositoryFactory.getWordSetRepository();
    }

    @Provides
    @Singleton
    public WordRepetitionProgressRepository wordRepetitionProgressRepository(RepositoryFactory repositoryFactory) {
        return repositoryFactory.getWordRepetitionProgressRepository();
    }

    @Provides
    @Singleton
    public SentenceRepository sentenceRepository(RepositoryFactory repositoryFactory) {
        return repositoryFactory.getSentenceRepository();
    }

    @Provides
    @Singleton
    public WordTranslationRepository wordTranslationRepository(RepositoryFactory repositoryFactory) {
        return repositoryFactory.getWordTranslationRepository();
    }

    @Provides
    @Singleton
    public ExpAuditRepository expAuditRepository(RepositoryFactory repositoryFactory) {
        return repositoryFactory.getExpAuditRepository();
    }

    @Provides
    @Singleton
    public TopicRepository topicRepository(RepositoryFactory repositoryFactory) {
        return repositoryFactory.getTopicRepository();
    }

    @Provides
    @Singleton
    public SentenceProvider sentenceProvider(WordSetRepository wordSetRepository,
                                             WordRepetitionProgressRepository wordRepetitionProgressRepository,
                                             SentenceRepository sentenceRepository,
                                             WordTranslationRepository wordTranslationRepository, DataServer server, ObjectMapper mapper) {
        SentenceProvider sentenceProvider = new SentenceProviderImpl(wordSetRepository, wordRepetitionProgressRepository, sentenceRepository, mapper);
        ServerSentenceProviderDecorator serverSentenceProviderDecorator = new ServerSentenceProviderDecorator(sentenceProvider, server);
        CachedSentenceProviderDecorator cachedSentenceProviderDecorator = new CachedSentenceProviderDecorator(serverSentenceProviderDecorator, sentenceRepository);
        WordTranslationSentenceProviderDecorator translationSentenceProviderDecorator = new WordTranslationSentenceProviderDecorator(cachedSentenceProviderDecorator, wordTranslationRepository);
        return new WordProgressSentenceProviderDecorator(translationSentenceProviderDecorator, wordSetRepository, wordRepetitionProgressRepository);
    }

    @Provides
    @Singleton
    public WordSetService wordSetService(WordSetRepository wordSetRepository, DataServer dataServer) {
        WordSetServiceImpl wordSetService = new WordSetServiceImpl(dataServer, wordSetRepository);
        return new CachedWordSetServiceDecorator(wordSetRepository, wordSetService);
    }
}
