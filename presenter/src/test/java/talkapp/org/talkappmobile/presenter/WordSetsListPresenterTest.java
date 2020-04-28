package talkapp.org.talkappmobile.presenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import talkapp.org.talkappmobile.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.view.WordSetsListView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WordSetsListPresenterTest {

    @Mock
    private StudyingWordSetsListInteractor interactor;
    @Mock
    private WordSetsListView view;

    @Test
    public void constructor_NPEInteractorNull() {
        // setup
        int topicId = 1;
        Topic topic = new Topic();
        topic.setId(topicId);

        // when
        WordSetsListPresenter presenter = new WordSetsListPresenterImpl(topic, view, interactor);
        presenter.initialize();

        // then
        assertEquals(topic, Whitebox.getInternalState(presenter, "topic"));
        assertEquals(view, Whitebox.getInternalState(presenter, "view"));
        assertNotNull(Whitebox.getInternalState(presenter, "interactor"));
    }

    @Test
    public void initialize_ordinaryCase() {
        // setup
        int topicId = 1;
        Topic topic = new Topic();
        topic.setId(topicId);

        // when
        WordSetsListPresenterImpl presenter = new WordSetsListPresenterImpl(topic, view, interactor);
        presenter.initialize();

        // then
        verify(view).onInitializeBeginning();
        verify(interactor).initializeWordSets(topic, presenter);
        verify(view).onInitializeEnd();
    }

    @Test(expected = RuntimeException.class)
    public void initialize_ordinaryException() {
        // setup
        int topicId = 1;
        Topic topic = new Topic();
        topic.setId(topicId);

        // when
        WordSetsListPresenterImpl presenter = new WordSetsListPresenterImpl(topic, view, interactor);
        doThrow(new RuntimeException()).when(view).onInitializeBeginning();
        try {
            presenter.initialize();
        } finally {
            // then
            verify(interactor, times(0)).initializeWordSets(topic, presenter);
            verify(view).onInitializeEnd();
        }
    }
}