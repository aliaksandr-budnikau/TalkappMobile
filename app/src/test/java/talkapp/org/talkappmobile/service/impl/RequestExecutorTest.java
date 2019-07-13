package talkapp.org.talkappmobile.service.impl;

import org.junit.Test;

import java.io.IOException;

import javax.net.ssl.SSLException;

import retrofit2.Call;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestExecutorTest {

    @Test(expected = InternetConnectionLostException.class)
    public void testExpectedInternetConnectionLostException() throws IOException {
        // setup
        RequestExecutor executor = new RequestExecutor();
        Call call = mock(Call.class);

        // when
        when(call.execute()).thenThrow(SSLException.class);
        executor.execute(call);
    }
}