package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.model.WordSet;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetVocabularyPresenterTest {

    @Mock
    private PracticeWordSetVocabularyInteractor interactor;
    @Mock
    private PracticeWordSetVocabularyView view;

    @Test
    public void onResume_ordinaryCase() {
        // setup
        WordSet wordSet = new WordSet();

        // when
        PracticeWordSetVocabularyPresenter presenter = new PracticeWordSetVocabularyPresenter(view, interactor);
        presenter.initialise(wordSet.getId());

        // then
        verify(view).onInitializeBeginning();
        verify(interactor).initialiseVocabulary(wordSet.getId(), presenter);
        verify(view).onInitializeEnd();
    }

    @Test(expected = RuntimeException.class)
    public void onResume_ordinaryException() {
        // setup
        WordSet wordSet = new WordSet();

        // when
        PracticeWordSetVocabularyPresenter presenter = new PracticeWordSetVocabularyPresenter(view, interactor);
        doThrow(new RuntimeException()).when(view).onInitializeBeginning();
        try {
            presenter.initialise(wordSet.getId());
        } finally {
            // then
            verify(interactor, times(0)).initialiseVocabulary(wordSet.getId(), presenter);
            verify(view).onInitializeEnd();
        }
    }
}