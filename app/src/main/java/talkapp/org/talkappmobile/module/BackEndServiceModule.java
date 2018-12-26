package talkapp.org.talkappmobile.module;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.SaveSharedPreference;
import talkapp.org.talkappmobile.component.backend.AccountRestClient;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.LoginRestClient;
import talkapp.org.talkappmobile.component.backend.SentenceRestClient;
import talkapp.org.talkappmobile.component.backend.TextGrammarCheckRestClient;
import talkapp.org.talkappmobile.component.backend.TopicRestClient;
import talkapp.org.talkappmobile.component.backend.WordSetRestClient;
import talkapp.org.talkappmobile.component.backend.WordTranslationRestClient;
import talkapp.org.talkappmobile.component.backend.impl.AuthorizationInterceptor;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerImpl;
import talkapp.org.talkappmobile.component.impl.LoggerImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
@EBean
public class BackEndServiceModule {

    public static final int TIMEOUT = 20;

    @Bean(LoggerImpl.class)
    Logger logger;
    @Bean(AuthSign.class)
    AuthSign authSign;

    @Provides
    @Singleton
    public WordSetRestClient provideWordSetService(Retrofit retrofit) {
        return retrofit.create(WordSetRestClient.class);
    }

    @Provides
    @Singleton
    public TopicRestClient provideTopicService(Retrofit retrofit) {
        return retrofit.create(TopicRestClient.class);
    }

    @Provides
    @Singleton
    public TextGrammarCheckRestClient provideTextGrammarCheckService(Retrofit retrofit) {
        return retrofit.create(TextGrammarCheckRestClient.class);
    }

    @Provides
    @Singleton
    public LoginRestClient provideLoginService(Retrofit retrofit) {
        return retrofit.create(LoginRestClient.class);
    }

    @Provides
    @Singleton
    public AccountRestClient provideUserService(Retrofit retrofit) {
        return retrofit.create(AccountRestClient.class);
    }

    @Provides
    @Singleton
    public SentenceRestClient provideSentenceService(Retrofit retrofit) {
        return retrofit.create(SentenceRestClient.class);
    }

    @Provides
    @Singleton
    public WordTranslationRestClient provideWordTranslationService(Retrofit retrofit) {
        return retrofit.create(WordTranslationRestClient.class);
    }

    @Provides
    @Singleton
    public BackendServer provideBackendServer(Context context, LoginRestClient loginRestClient, TopicRestClient topicRestClient, SentenceRestClient sentenceRestClient, WordSetRestClient wordSetRestClient, TextGrammarCheckRestClient checkRestClient, WordTranslationRestClient wordTranslationRestClient, AccountRestClient accountRestClient, SaveSharedPreference saveSharedPreference) {
        return new BackendServerImpl(logger, authSign, context, accountRestClient, loginRestClient, sentenceRestClient, checkRestClient, topicRestClient, wordSetRestClient, wordTranslationRestClient, saveSharedPreference);
    }

    @Provides
    @Singleton
    public AuthorizationInterceptor provideAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Provides
    @Singleton
    public JacksonConverterFactory provideJacksonConverterFactory(ObjectMapper mapper) {
        return JacksonConverterFactory.create(mapper);
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(AuthorizationInterceptor authorizationInterceptor) {
        return new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(authorizationInterceptor).build();
    }

    @Provides
    public Retrofit provideRetrofit(@Named("serverUrl") String serverUrl, OkHttpClient okHttpClient, JacksonConverterFactory jacksonConverterFactory) {
        return new Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(jacksonConverterFactory)
                .build();
    }

    @Provides
    @Named("serverUrl")
    public String provideServerUrl() {
        return "http://192.168.0.101:8080";
    }
}