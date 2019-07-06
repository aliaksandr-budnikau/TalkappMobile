package org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.talkappmobile.service.BackendServerFactory;
import org.talkappmobile.service.DataServer;
import org.talkappmobile.service.GitHubRestClient;
import org.talkappmobile.service.Logger;
import org.talkappmobile.service.SentenceRestClient;
import org.talkappmobile.service.ServiceFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@EBean(scope = EBean.Scope.Singleton)
public class BackendServerFactoryBean implements BackendServerFactory {

    public static final int TIMEOUT = 5;
    public static final String SERVER_URL = "http://192.168.0.101:8080";
    public static final String GIT_HUB_URL = "https://raw.githubusercontent.com";

    @Bean(LoggerBean.class)
    Logger logger;
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
        backendServer = new DataServerImpl(
                sentenceRestClient(),
                gitHubRestClient(),
                serviceFactory.getLocalDataService(), requestExecutor
        );
        return backendServer;
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
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS).build();
    }

    private JacksonConverterFactory jacksonConverterFactory() {
        return JacksonConverterFactory.create(mapper());
    }

    private ObjectMapper mapper() {
        return new ObjectMapper();
    }
}