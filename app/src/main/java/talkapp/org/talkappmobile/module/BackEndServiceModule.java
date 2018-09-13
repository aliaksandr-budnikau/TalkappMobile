package talkapp.org.talkappmobile.module;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import talkapp.org.talkappmobile.service.backend.LoginService;
import talkapp.org.talkappmobile.service.backend.RefereeService;
import talkapp.org.talkappmobile.service.backend.SentenceService;
import talkapp.org.talkappmobile.service.backend.AccountService;
import talkapp.org.talkappmobile.service.backend.TopicService;
import talkapp.org.talkappmobile.service.backend.VoiceService;
import talkapp.org.talkappmobile.service.backend.WordSetExperienceService;
import talkapp.org.talkappmobile.service.backend.WordSetService;

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
    public Retrofit provideRetrofit(@Named("serverUrl") String serverUrl) {
        return new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Provides
    @Named("serverUrl")
    public String provideServerUrl() {
        return "http://192.168.0.102:8080";
    }
}