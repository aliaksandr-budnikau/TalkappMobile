package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.activity.view.TopicsFragmentView;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TopicsFragmentPresenterTest {
    @Mock
    private TopicsFragmentInteractor interactor;
    @Mock
    private TopicsFragmentView view;

    @Test
    public void initialize_ordinaryCase() {
        // when
        TopicsFragmentPresenter presenter = new TopicsFragmentPresenter(view, interactor);
        presenter.initialize();

        // then
        verify(view).onInitializeBeginning();
        verify(interactor).loadTopics(presenter);
        verify(view).onInitializeEnd();
    }

    @Test(expected = RuntimeException.class)
    public void initialize_ordinaryException() {
        // when
        TopicsFragmentPresenter presenter = new TopicsFragmentPresenter(view, interactor);
        doThrow(new RuntimeException()).when(view).onInitializeBeginning();
        try {
            presenter.initialize();
        } finally {
            // then
            verify(interactor, times(0)).loadTopics(presenter);
            verify(view).onInitializeEnd();
        }
    }
}