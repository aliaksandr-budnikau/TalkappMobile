package talkapp.org.talkappmobile.component.backend.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.AccountRestClient;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.LoginRestClient;
import talkapp.org.talkappmobile.component.backend.SentenceRestClient;
import talkapp.org.talkappmobile.component.backend.TextGrammarCheckRestClient;
import talkapp.org.talkappmobile.component.backend.TopicRestClient;
import talkapp.org.talkappmobile.component.backend.WordSetRestClient;
import talkapp.org.talkappmobile.component.backend.WordTranslationRestClient;
import talkapp.org.talkappmobile.component.impl.LoggerBean;

@EBean(scope = EBean.Scope.Singleton)
public class BackendServerFactoryBean implements BackendServerFactory {

    public static final int TIMEOUT = 20;
    public static final String SERVER_URL = "http://192.168.0.101:8080";

    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(AuthSign.class)
    AuthSign authSign;
    @Bean(AuthorizationInterceptor.class)
    AuthorizationInterceptor authorizationInterceptor;
    private Retrofit retrofit;
    private BackendServerImpl backendServer;

    @Override
    public synchronized BackendServer get() {
        if (backendServer != null) {
            return backendServer;
        }
        backendServer = new BackendServerImpl(logger, authSign,
                accountRestClient(),
                loginRestClient(),
                sentenceRestClient(),
                checkRestClient(), topicRestClient(), wordSetRestClient(), wordTranslationRestClient());
        return backendServer;
    }

    private WordSetRestClient wordSetRestClient() {
        return retrofit().create(WordSetRestClient.class);
    }

    private TopicRestClient topicRestClient() {
        return retrofit().create(TopicRestClient.class);
    }

    private TextGrammarCheckRestClient checkRestClient() {
        return retrofit().create(TextGrammarCheckRestClient.class);
    }

    private LoginRestClient loginRestClient() {
        return retrofit().create(LoginRestClient.class);
    }

    private AccountRestClient accountRestClient() {
        return retrofit().create(AccountRestClient.class);
    }

    private SentenceRestClient sentenceRestClient() {
        return retrofit().create(SentenceRestClient.class);
    }

    private WordTranslationRestClient wordTranslationRestClient() {
        return retrofit().create(WordTranslationRestClient.class);
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

    private OkHttpClient okHttpClient() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(authorizationInterceptor).build();
    }

    private JacksonConverterFactory jacksonConverterFactory() {
        return JacksonConverterFactory.create(mapper());
    }

    private ObjectMapper mapper() {
        return new ObjectMapper();
    }
}