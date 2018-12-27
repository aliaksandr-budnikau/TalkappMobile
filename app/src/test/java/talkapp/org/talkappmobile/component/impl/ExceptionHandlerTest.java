package talkapp.org.talkappmobile.component.impl;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;

import retrofit2.Call;
import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.presenter.ClassForInjection;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.InfraComponentsFactory;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.TopicRestClient;
import talkapp.org.talkappmobile.component.backend.impl.AuthorizationInterceptor;
import talkapp.org.talkappmobile.component.backend.impl.InternetConnectionLostException;
import talkapp.org.talkappmobile.module.TestBackEndServiceModule;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionHandlerTest {

    private BackendServer server;
    private InfraComponentsFactory componentsFactory;
    private ExceptionHandlerInteractor interactor;
    private TopicRestClient topicRestClient;
    @Mock
    private Context context;

    @Before
    public void setup() {
        TestBackEndServiceModule backEndServiceModule = new TestBackEndServiceModule();
        LoggerBean loggerBean = mock(LoggerBean.class);
        Whitebox.setInternalState(backEndServiceModule, "logger", loggerBean);
        Whitebox.setInternalState(backEndServiceModule, "authSign", mock(AuthSign.class));
        Whitebox.setInternalState(backEndServiceModule, "authorizationInterceptor", new AuthorizationInterceptor());
        ClassForInjection injection = new ClassForInjection(backEndServiceModule);
        server = injection.getServer();
        componentsFactory = injection.getComponentsFactory();
        interactor = new ExceptionHandlerInteractor(loggerBean);
        topicRestClient = injection.getTopicRestClient();
    }

    @Test
    public void test_ConnectException() throws IOException {
        ExceptionHandlerView view = mock(ExceptionHandlerView.class);
        Thread.UncaughtExceptionHandler exceptionHandler = componentsFactory.createExceptionHandler(context, view, interactor);

        Call call = mock(Call.class);
        when(call.execute()).thenThrow(ConnectException.class);
        when(topicRestClient.findAll(ArgumentMatchers.<Map<String, String>>any())).thenReturn(call);

        try {
            server.findAllTopics();
        } catch (InternetConnectionLostException e) {
            exceptionHandler.uncaughtException(Thread.currentThread(), e);
            verify(view, times(0)).killCurrentActivity();
            verify(view, times(0)).openCrashActivity(context, e, "Internet connection was lost");
            verify(view, times(0)).openLoginActivity(context);
            verify(view).showToastMessage("Internet connection was lost");
            return;
        }
        fail();
    }

    @Test
    public void test_SocketTimeoutException() throws IOException {
        ExceptionHandlerView view = mock(ExceptionHandlerView.class);
        Thread.UncaughtExceptionHandler exceptionHandler = componentsFactory.createExceptionHandler(context, view, interactor);

        Call call = mock(Call.class);
        when(call.execute()).thenThrow(SocketTimeoutException.class);
        when(topicRestClient.findAll(ArgumentMatchers.<Map<String, String>>any())).thenReturn(call);

        try {
            server.findAllTopics();
        } catch (InternetConnectionLostException e) {
            exceptionHandler.uncaughtException(Thread.currentThread(), e);
            verify(view, times(0)).killCurrentActivity();
            verify(view, times(0)).openCrashActivity(context, e, "Internet connection was lost");
            verify(view, times(0)).openLoginActivity(context);
            verify(view).showToastMessage("Internet connection was lost");
            return;
        }
        fail();
    }


    @Test
    public void test_RuntimeException() throws IOException {
        ExceptionHandlerView view = mock(ExceptionHandlerView.class);
        Thread.UncaughtExceptionHandler exceptionHandler = componentsFactory.createExceptionHandler(context, view, interactor);

        Call call = mock(Call.class);
        when(call.execute()).thenThrow(RuntimeException.class);
        when(topicRestClient.findAll(ArgumentMatchers.<Map<String, String>>any())).thenReturn(call);

        try {
            server.findAllTopics();
        } catch (RuntimeException e) {
            exceptionHandler.uncaughtException(Thread.currentThread(), e);
            verify(view).openCrashActivity(eq(context), any(Throwable.class), anyString());
            verify(view).killCurrentActivity();
            verify(view, times(0)).openLoginActivity(context);
            verify(view, times(0)).showToastMessage("Internet connection was lost");
            return;
        }
        fail();
    }

    @Test
    public void test_AuthorizationException() {
        ExceptionHandlerView view = mock(ExceptionHandlerView.class);
        Thread.UncaughtExceptionHandler exceptionHandler = componentsFactory.createExceptionHandler(context, view, interactor);

        try {
            server.findAllWordSets();
        } catch (Exception e) {
            exceptionHandler.uncaughtException(Thread.currentThread(), e);
            verify(view, times(0)).openCrashActivity(context, e, "Internet connection was lost");
            verify(view, times(0)).showToastMessage("Internet connection was lost");
            verify(view).openLoginActivity(context);
            verify(view).killCurrentActivity();
            return;
        }
        fail();
    }
}