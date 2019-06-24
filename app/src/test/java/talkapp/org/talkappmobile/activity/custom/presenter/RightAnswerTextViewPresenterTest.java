package talkapp.org.talkappmobile.activity.custom.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.activity.custom.interactor.RightAnswerTextViewInteractor;
import talkapp.org.talkappmobile.activity.custom.view.RightAnswerTextViewView;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

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
        Word2Tokens word = new Word2Tokens("dsfds54e3whggfdA", 3);
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