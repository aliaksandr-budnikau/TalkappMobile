package talkapp.org.talkappmobile.service.impl;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import javax.net.ssl.SSLException;

import retrofit2.Call;
import talkapp.org.talkappmobile.service.InternetConnectionLostException;
import talkapp.org.talkappmobile.service.RequestExecutor;

public class RequestExecutorTest {

    @Test(expected = InternetConnectionLostException.class)
    public void testExpectedInternetConnectionLostException() throws IOException {
        // setup
        RequestExecutor executor = new RequestExecutor();
        Call call = Mockito.mock(Call.class);

        // when
        Mockito.when(call.execute()).thenThrow(SSLException.class);
        executor.execute(call);
    }
}