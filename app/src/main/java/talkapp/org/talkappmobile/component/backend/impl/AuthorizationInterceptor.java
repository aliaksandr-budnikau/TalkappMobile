package talkapp.org.talkappmobile.component.backend.impl;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response mainResponse = chain.proceed(chain.request());
        List<String> pathSegments = chain.request().url().pathSegments();
        if (pathSegments == null || pathSegments.isEmpty() || pathSegments.get(0).equals("login")) {
            return mainResponse;
        }
        if (mainResponse.code() == 401) {
            throw new AuthorizationException("Authorization is required!");
        }
        return mainResponse;
    }
}