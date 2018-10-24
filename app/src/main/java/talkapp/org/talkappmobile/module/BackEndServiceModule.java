package talkapp.org.talkappmobile.module;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import talkapp.org.talkappmobile.component.AuthSign;
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

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class BackEndServiceModule {

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
    public BackendServer provideBackendServer(AuthSign authSign, Context context, LoginRestClient loginRestClient, TopicRestClient topicRestClient, SentenceRestClient sentenceRestClient, WordSetRestClient wordSetRestClient, TextGrammarCheckRestClient checkRestClient, WordTranslationRestClient wordTranslationRestClient, AccountRestClient accountRestClient, SaveSharedPreference saveSharedPreference) {
        return new BackendServerImpl(authSign, context, accountRestClient, loginRestClient, sentenceRestClient, checkRestClient, topicRestClient, wordSetRestClient, wordTranslationRestClient, saveSharedPreference);
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
        return new OkHttpClient().newBuilder().addInterceptor(authorizationInterceptor).build();
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
        return "http://192.168.0.100:8080";
    }
}