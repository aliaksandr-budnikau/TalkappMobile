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
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.GitHubRestClient;
import talkapp.org.talkappmobile.component.backend.LoginRestClient;
import talkapp.org.talkappmobile.component.backend.SentenceRestClient;
import talkapp.org.talkappmobile.component.backend.TextGrammarCheckRestClient;
import talkapp.org.talkappmobile.component.backend.WordSetRestClient;
import talkapp.org.talkappmobile.component.database.ServiceFactory;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.impl.LoggerBean;

@EBean(scope = EBean.Scope.Singleton)
public class BackendServerFactoryBean implements BackendServerFactory {

    public static final int TIMEOUT = 5;
    public static final String SERVER_URL = "http://192.168.0.101:8080";
    public static final String GIT_HUB_URL = "https://raw.githubusercontent.com";

    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(AuthSign.class)
    AuthSign authSign;
    @Bean(AuthorizationInterceptor.class)
    AuthorizationInterceptor authorizationInterceptor;
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @Bean
    RequestExecutor requestExecutor;
    private Retrofit retrofit;
    private Retrofit gitHubRetrofit;
    private DataServerImpl backendServer;

    @Override
    public synchronized DataServer get() {
        if (backendServer != null) {
            return backendServer;
        }
        backendServer = new DataServerImpl(logger, authSign,
                accountRestClient(),
                loginRestClient(),
                sentenceRestClient(),
                gitHubRestClient(),
                checkRestClient(), wordSetRestClient(),
                serviceFactory.getLocalDataService(), requestExecutor
        );
        return backendServer;
    }

    private WordSetRestClient wordSetRestClient() {
        return retrofit().create(WordSetRestClient.class);
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

    private GitHubRestClient gitHubRestClient() {
        return gitHubRetrofit().create(GitHubRestClient.class);
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