package talkapp.org.talkappmobile.bean;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.WordSetService;

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
        return "http://10.0.2.2:8080";
    }
}