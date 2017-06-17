package talkapp.org.talkappmobile.bean;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import talkapp.org.talkappmobile.service.WordSetService;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class BackEndServiceModule {

    @Provides
    @Singleton
    public WordSetService provideWordSetService(@Named("retrofit") Retrofit retrofit) {
        return retrofit.create(WordSetService.class);
    }

    @Provides
    @Named("retrofit")
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