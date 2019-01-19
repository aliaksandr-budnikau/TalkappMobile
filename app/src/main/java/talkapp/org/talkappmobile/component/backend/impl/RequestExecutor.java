package talkapp.org.talkappmobile.component.backend.impl;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.impl.LoggerBean;

@EBean(scope = EBean.Scope.Singleton)
public class RequestExecutor {
    private static final String TAG = RequestExecutor.class.getSimpleName();
    @Bean(LoggerBean.class)
    Logger logger;

    public <T> Response<T> execute(Call<T> call) {
        try {
            return call.execute();
        } catch (SocketException | UnknownHostException | SocketTimeoutException e) {
            throw new InternetConnectionLostException("Internet connection was lost");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}