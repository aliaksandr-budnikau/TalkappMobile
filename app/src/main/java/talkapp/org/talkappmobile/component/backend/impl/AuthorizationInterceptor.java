package talkapp.org.talkappmobile.component.backend.impl;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response mainResponse = chain.proceed(chain.request());
        if (mainResponse.code() == 401 || mainResponse.code() == 403) {
            throw new AuthorizationException("Authorization is required!");
        }
        return mainResponse;
    }
}