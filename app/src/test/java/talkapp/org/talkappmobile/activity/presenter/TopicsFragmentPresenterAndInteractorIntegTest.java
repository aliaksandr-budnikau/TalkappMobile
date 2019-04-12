package talkapp.org.talkappmobile.activity.presenter;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.activity.view.TopicsFragmentView;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.backend.impl.RequestExecutor;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordTranslationDao;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.model.Topic;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TopicsFragmentPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    @Mock
    private TopicsFragmentView view;
    private TopicsFragmentInteractor topicsFragmentInteractor;

    @Before
    public void setup() {
        LocalDataServiceImpl localDataService = new LocalDataServiceImpl(mock(WordSetDao.class), mock(TopicDao.class), mock(SentenceDao.class), mock(WordTranslationDao.class), new ObjectMapper(), new LoggerBean());

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        DataServer server = factory.get();

        topicsFragmentInteractor = new TopicsFragmentInteractor(server);
    }

    @Test
    public void test() {
        TopicsFragmentPresenter presenter = new TopicsFragmentPresenter(view, topicsFragmentInteractor);
        presenter.initialize();
        ArgumentCaptor<List<Topic>> topicsCaptor = forClass(List.class);
        verify(view).setTopics(topicsCaptor.capture());
        assertFalse(topicsCaptor.getValue().isEmpty());
        reset(view);

        List<Topic> topics = topicsCaptor.getValue();
        presenter.onTopicClick(topics.get(0));
        verify(view).openTopicWordSetsFragment(topics.get(0));
    }
}