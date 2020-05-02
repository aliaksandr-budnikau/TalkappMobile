package talkapp.org.talkappmobile.service;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;
import javax.net.ssl.SSLException;

import retrofit2.Call;
import retrofit2.Response;

public class RequestExecutor {

    @Inject
    public RequestExecutor() {
    }

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