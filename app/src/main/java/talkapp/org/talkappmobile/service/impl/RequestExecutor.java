package talkapp.org.talkappmobile.service.impl;

import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import retrofit2.Call;
import retrofit2.Response;

@EBean(scope = EBean.Scope.Singleton)
public class RequestExecutor {

    public <T> Response<T> execute(Call<T> call) {
        try {
            return call.execute();
        } catch (SocketException | UnknownHostException | SSLException | SocketTimeoutException e) {
            throw new InternetConnectionLostException("Internet connection was lost", e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}