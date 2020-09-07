package talkapp.org.talkappmobile.presenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.view.PracticeWordSetVocabularyView;

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
        PracticeWordSetVocabularyPresenterImpl presenter = new PracticeWordSetVocabularyPresenterImpl(view, interactor);
        presenter.initialise(wordSet);

        // then
        verify(view).onInitializeBeginning();
        verify(interactor).initialiseVocabulary(wordSet, presenter);
        verify(view).onInitializeEnd();
    }

    @Test(expected = RuntimeException.class)
    public void onResume_ordinaryException() {
        // setup
        WordSet wordSet = new WordSet();

        // when
        PracticeWordSetVocabularyPresenterImpl presenter = new PracticeWordSetVocabularyPresenterImpl(view, interactor);
        doThrow(new RuntimeException()).when(view).onInitializeBeginning();
        try {
            presenter.initialise(wordSet);
        } finally {
            // then
            verify(interactor, times(0)).initialiseVocabulary(wordSet, presenter);
            verify(view).onInitializeEnd();
        }
    }
}