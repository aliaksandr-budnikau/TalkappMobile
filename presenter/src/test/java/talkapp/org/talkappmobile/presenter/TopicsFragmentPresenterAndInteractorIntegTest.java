package talkapp.org.talkappmobile.presenter;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import talkapp.org.talkappmobile.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryImpl;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;
import talkapp.org.talkappmobile.view.TopicsFragmentView;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class TopicsFragmentPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    private TopicsFragmentView view;
    private TopicsFragmentInteractor topicsFragmentInteractor;
    private ServiceFactory serviceFactory;

    @Before
    public void setup() {
        view = mock(TopicsFragmentView.class);


        RepositoryFactory repositoryFactory = new RepositoryFactoryImpl(RuntimeEnvironment.application);
        serviceFactory = new ServiceFactoryImpl(repositoryFactory);

        topicsFragmentInteractor = new TopicsFragmentInteractor(serviceFactory.getTopicService());
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void test() {
        TopicsFragmentPresenter presenter = new TopicsFragmentPresenterImpl(view, topicsFragmentInteractor);
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