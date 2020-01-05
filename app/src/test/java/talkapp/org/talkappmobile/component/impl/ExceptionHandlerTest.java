package talkapp.org.talkappmobile.component.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.service.CachedDataServerDecorator;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.GitHubRestClient;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.InternetConnectionLostException;
import talkapp.org.talkappmobile.service.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RequestExecutor;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;

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

    private CachedDataServerDecorator server;
    private ExceptionHandlerInteractor interactor;
    private GitHubRestClient gitHubRestClient;

    @Mock
    private TopicDao topicDao;
    @Mock
    private SentenceDao sentenceDao;
    @Mock
    private WordTranslationDao wordTranslationDao;

    @Before
    public void setup() {
        LoggerBean loggerBean = mock(LoggerBean.class);
        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", loggerBean);
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(new LocalDataServiceImpl(topicDao, sentenceDao, wordTranslationDao, new ObjectMapper()));
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        RequestExecutor requestExecutor = new RequestExecutor();
        Whitebox.setInternalState(factory, "requestExecutor", requestExecutor);
        server = (CachedDataServerDecorator) factory.get();

        interactor = new ExceptionHandlerInteractor(loggerBean);
        gitHubRestClient = mock(GitHubRestClient.class);
        Whitebox.setInternalState(server.getServer(), "gitHubRestClient", gitHubRestClient);
    }

    @Test
    public void test_ConnectException() throws IOException {
        ExceptionHandlerView view = mock(ExceptionHandlerView.class);
        Thread.UncaughtExceptionHandler exceptionHandler = new ExceptionHandler(view, interactor);

        Call call = mock(Call.class);
        when(call.execute()).thenThrow(ConnectException.class);
        when(gitHubRestClient.findAllTopics()).thenReturn(call);

        try {
            server.findAllTopics();
        } catch (InternetConnectionLostException e) {
            fail();
        }
        verify(topicDao).findAll();
        verify(view, times(0)).killCurrentActivity();
        verify(view, times(0)).openCrashActivity(any(Exception.class), eq("Internet connection was lost"));
        verify(view, times(0)).showToastMessage("Internet connection was lost");
    }

    @Test
    public void test_SocketTimeoutException() throws IOException {
        ExceptionHandlerView view = mock(ExceptionHandlerView.class);
        Thread.UncaughtExceptionHandler exceptionHandler = new ExceptionHandler(view, interactor);

        Call call = mock(Call.class);
        when(call.execute()).thenThrow(SocketTimeoutException.class);
        when(gitHubRestClient.findAllTopics()).thenReturn(call);

        try {
            server.findAllTopics();
        } catch (InternetConnectionLostException e) {
            fail();
        }
        verify(topicDao).findAll();
        verify(view, times(0)).killCurrentActivity();
        verify(view, times(0)).openCrashActivity(any(Exception.class), eq("Internet connection was lost"));
        verify(view, times(0)).showToastMessage("Internet connection was lost");
    }


    @Test
    public void test_RuntimeException() throws IOException {
        ExceptionHandlerView view = mock(ExceptionHandlerView.class);
        Thread.UncaughtExceptionHandler exceptionHandler = new ExceptionHandler(view, interactor);

        Call call = mock(Call.class);
        when(call.execute()).thenThrow(RuntimeException.class);
        when(gitHubRestClient.findAllTopics()).thenReturn(call);

        try {
            server.findAllTopics();
        } catch (RuntimeException e) {
            exceptionHandler.uncaughtException(Thread.currentThread(), e);
            verify(view).openCrashActivity(any(Throwable.class), anyString());
            verify(view).killCurrentActivity();
            verify(view, times(0)).showToastMessage("Internet connection was lost");
            return;
        }
        fail();
    }
}