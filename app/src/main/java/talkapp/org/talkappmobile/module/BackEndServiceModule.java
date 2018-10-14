package talkapp.org.talkappmobile.module;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import talkapp.org.talkappmobile.component.backend.AccountService;
import talkapp.org.talkappmobile.component.backend.LoginService;
import talkapp.org.talkappmobile.component.backend.SentenceService;
import talkapp.org.talkappmobile.component.backend.TextGrammarCheckService;
import talkapp.org.talkappmobile.component.backend.TopicService;
import talkapp.org.talkappmobile.component.backend.WordSetService;
import talkapp.org.talkappmobile.component.backend.WordTranslationService;
import talkapp.org.talkappmobile.component.backend.impl.AuthorizationInterceptor;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class BackEndServiceModule {

    @Provides
    @Singleton
    public WordSetService provideWordSetService(Retrofit retrofit) {
        return retrofit.create(WordSetService.class);
    }

    @Provides
    @Singleton
    public TopicService provideTopicService(Retrofit retrofit) {
        return retrofit.create(TopicService.class);
    }

    @Provides
    @Singleton
    public TextGrammarCheckService provideTextGrammarCheckService(Retrofit retrofit) {
        return retrofit.create(TextGrammarCheckService.class);
    }

    @Provides
    @Singleton
    public LoginService provideLoginService(Retrofit retrofit) {
        return retrofit.create(LoginService.class);
    }

    @Provides
    @Singleton
    public AccountService provideUserService(Retrofit retrofit) {
        return retrofit.create(AccountService.class);
    }

    @Provides
    @Singleton
    public SentenceService provideSentenceService(Retrofit retrofit) {
        return retrofit.create(SentenceService.class);
    }

    @Provides
    @Singleton
    public WordTranslationService provideWordTranslationService(Retrofit retrofit) {
        return retrofit.create(WordTranslationService.class);
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