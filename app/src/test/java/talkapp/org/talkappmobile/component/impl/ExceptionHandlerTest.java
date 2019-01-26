package talkapp.org.talkappmobile.component.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.GitHubRestClient;
import talkapp.org.talkappmobile.component.backend.impl.AuthorizationInterceptor;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.backend.impl.InternetConnectionLostException;
import talkapp.org.talkappmobile.component.backend.impl.RequestExecutor;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordTranslationDao;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.model.Sentence;

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

    private DataServer server;
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
        Whitebox.setInternalState(factory, "authSign", mock(AuthSign.class));
        Whitebox.setInternalState(factory, "authorizationInterceptor", new AuthorizationInterceptor());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(new LocalDataServiceImpl(mock(WordSetDao.class), topicDao, sentenceDao, wordTranslationDao, new ObjectMapper(), new LoggerBean()));
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        RequestExecutor requestExecutor = new RequestExecutor();
        Whitebox.setInternalState(requestExecutor, "logger", new LoggerBean());
        Whitebox.setInternalState(factory, "requestExecutor", requestExecutor);
        server = factory.get();

        interactor = new ExceptionHandlerInteractor(loggerBean);
        gitHubRestClient = mock(GitHubRestClient.class);
        Whitebox.setInternalState(server, "gitHubRestClient", gitHubRestClient);
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
        verify(view, times(0)).openLoginActivity();
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
        verify(view, times(0)).openLoginActivity();
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
            verify(view, times(0)).openLoginActivity();
            verify(view, times(0)).showToastMessage("Internet connection was lost");
            return;
        }
        fail();
    }

    @Test
    public void test_AuthorizationException() {
        ExceptionHandlerView view = mock(ExceptionHandlerView.class);
        Thread.UncaughtExceptionHandler exceptionHandler = new ExceptionHandler(view, interactor);

        try {
            server.saveSentenceScore(new Sentence());
        } catch (Exception e) {
            exceptionHandler.uncaughtException(Thread.currentThread(), e);
            verify(view, times(0)).openCrashActivity(e, "Internet connection was lost");
            verify(view, times(0)).showToastMessage("Internet connection was lost");
            verify(view).openLoginActivity();
            verify(view).killCurrentActivity();
            return;
        }
        fail();
    }
}