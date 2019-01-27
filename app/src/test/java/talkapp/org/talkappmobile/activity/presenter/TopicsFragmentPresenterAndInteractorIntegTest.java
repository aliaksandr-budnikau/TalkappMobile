package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.activity.view.TopicsFragmentView;
import talkapp.org.talkappmobile.model.Topic;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TopicsFragmentPresenterAndInteractorIntegTest extends PresenterAndInteractorIntegTest {
    @Mock
    private TopicsFragmentView view;
    private TopicsFragmentInteractor topicsFragmentInteractor;

    @Before
    public void setup() {
        topicsFragmentInteractor = new TopicsFragmentInteractor(getServer());
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