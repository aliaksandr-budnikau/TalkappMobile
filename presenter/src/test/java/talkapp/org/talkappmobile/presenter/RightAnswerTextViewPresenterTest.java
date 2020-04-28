package talkapp.org.talkappmobile.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.interactor.RightAnswerTextViewInteractor;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.view.RightAnswerTextViewView;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RightAnswerTextViewPresenterTest {

    @Mock
    private RightAnswerTextViewView view;
    private RightAnswerTextViewPresenter presenter;

    @Before
    public void init() {
        RightAnswerTextViewInteractor interactor = new RightAnswerTextViewInteractor(new TextUtilsImpl());
        presenter = new RightAnswerTextViewPresenter(interactor, view);
        Sentence sentence = new Sentence();
        sentence.setText("dsd");
        String wordAsString = "dsfds54e3whggfdA";
        Word2Tokens word = new Word2Tokens(wordAsString, wordAsString, 3);
        presenter.setModel(sentence, word);
    }

    @Test
    public void rightAnswerTouched_ButLocked() {
        // when
        presenter.lock();
        presenter.rightAnswerTouched();

        // then
        verify(view, times(0)).onNewValue(anyString());
        verify(view, times(0)).answerHasBeenSeen();
    }

    @Test
    public void rightAnswerTouched_ButNotLocked() {
        // when
        presenter.rightAnswerTouched();

        // then
        verify(view).onNewValue(anyString());
        verify(view).answerHasBeenSeen();
    }
}