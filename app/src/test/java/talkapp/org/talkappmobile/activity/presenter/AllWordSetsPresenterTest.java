package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import talkapp.org.talkappmobile.activity.interactor.AllWordSetsInteractor;
import talkapp.org.talkappmobile.activity.view.AllWordSetsView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AllWordSetsPresenterTest {

    @Mock
    private AllWordSetsInteractor interactor;
    @Mock
    private AllWordSetsView view;

    @Test
    public void constructor_NPEInteractorNull() {
        // setup
        int topicId = 1;

        // when
        AllWordSetsPresenter presenter = new AllWordSetsPresenter(topicId, view, interactor);
        presenter.initialize();

        // then
        assertEquals(topicId, Whitebox.getInternalState(presenter, "topicId"));
        assertEquals(view, Whitebox.getInternalState(presenter, "view"));
        assertNotNull(Whitebox.getInternalState(presenter, "interactor"));
    }

    @Test
    public void initialize_ordinaryCase() {
        // setup
        int topicId = 1;

        // when
        AllWordSetsPresenter presenter = new AllWordSetsPresenter(topicId, view, interactor);
        presenter.initialize();

        // then
        verify(view).onInitializeBeginning();
        verify(interactor).initializeWordSets(topicId, presenter);
        verify(view).onInitializeEnd();
    }

    @Test(expected = RuntimeException.class)
    public void initialize_ordinaryException() {
        // setup
        int topicId = 1;

        // when
        AllWordSetsPresenter presenter = new AllWordSetsPresenter(topicId, view, interactor);
        doThrow(new RuntimeException()).when(view).onInitializeBeginning();
        try {
            presenter.initialize();
        } finally {
            // then
            verify(interactor, times(0)).initializeWordSets(topicId, presenter);
            verify(view).onInitializeEnd();
        }
    }
}