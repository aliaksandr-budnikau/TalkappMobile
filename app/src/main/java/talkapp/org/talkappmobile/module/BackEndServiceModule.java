package talkapp.org.talkappmobile.module;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import talkapp.org.talkappmobile.component.backend.AccountService;
import talkapp.org.talkappmobile.component.backend.LoginService;
import talkapp.org.talkappmobile.component.backend.RefereeService;
import talkapp.org.talkappmobile.component.backend.SentenceService;
import talkapp.org.talkappmobile.component.backend.TopicService;
import talkapp.org.talkappmobile.component.backend.VoiceService;
import talkapp.org.talkappmobile.component.backend.WordSetExperienceService;
import talkapp.org.talkappmobile.component.backend.WordSetService;
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
    public WordSetExperienceService provideWordSetExperienceService(Retrofit retrofit) {
        return retrofit.create(WordSetExperienceService.class);
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
    public VoiceService provideVoiceService(Retrofit retrofit) {
        return retrofit.create(VoiceService.class);
    }

    @Provides
    @Singleton
    public RefereeService provideRefereeService(Retrofit retrofit) {
        return retrofit.create(RefereeService.class);
    }

    @Provides
    @Singleton
    public SentenceService provideSentenceService(Retrofit retrofit) {
        return retrofit.create(SentenceService.class);
    }

    @Provides
    @Singleton
    public AuthorizationInterceptor provideAuthorizationInterceptor() {
        return new AuthorizationInterceptor();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(AuthorizationInterceptor authorizationInterceptor) {
        return new OkHttpClient().newBuilder().addInterceptor(authorizationInterceptor).build();
    }

    @Provides
    public Retrofit provideRetrofit(@Named("serverUrl") String serverUrl, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Provides
    @Named("serverUrl")
    public String provideServerUrl() {
        return "http://192.168.0.100:8080";
    }
}