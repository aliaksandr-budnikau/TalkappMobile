package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


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
}