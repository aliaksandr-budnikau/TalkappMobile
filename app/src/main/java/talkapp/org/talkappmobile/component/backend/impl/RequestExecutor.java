package talkapp.org.talkappmobile.component.backend.impl;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.component.Logger;

public class RequestExecutor {
    private static final String TAG = RequestExecutor.class.getSimpleName();
    private final Logger logger;

    public RequestExecutor(Logger logger) {
        this.logger = logger;
    }

    public <T> Response<T> execute(Call<T> call) {
        try {
            return call.execute();
        } catch (SocketException | SocketTimeoutException e) {
            logger.e(TAG, e, e.getMessage());
            throw new InternetConnectionLostException("Internet connection was lost");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}