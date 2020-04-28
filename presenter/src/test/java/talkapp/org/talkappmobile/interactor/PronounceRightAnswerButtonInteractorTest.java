package talkapp.org.talkappmobile.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.presenter.PronounceRightAnswerButtonPresenter;
import talkapp.org.talkappmobile.presenter.PronounceRightAnswerButtonPresenterImpl;
import talkapp.org.talkappmobile.view.PronounceRightAnswerButtonView;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PronounceRightAnswerButtonInteractorTest {
    @Mock
    private PronounceRightAnswerButtonView view;
    private PronounceRightAnswerButtonPresenter presenter;

    @Before
    public void init() {
        PronounceRightAnswerButtonInteractor interactor = new PronounceRightAnswerButtonInteractor();
        presenter = new PronounceRightAnswerButtonPresenterImpl(interactor, view);
        Sentence sentence = new Sentence();
        sentence.setText("dsd");
        String wordAsString = "dsfds54e3whggfdA";
        Word2Tokens word = new Word2Tokens(wordAsString, wordAsString, 3);
        presenter.setModel(sentence);
    }

    @Test
    public void pronounceRightAnswerButtonClick_ButLocked() {
        // when
        presenter.lock();
        presenter.pronounceRightAnswerButtonClick();

        // then
        verify(view, times(0)).onAnswerHasBeenRevealed();
    }

    @Test
    public void pronounceRightAnswerButtonClick_ButNotLocked() {
        // when
        presenter.pronounceRightAnswerButtonClick();

        // then
        verify(view).onAnswerHasBeenRevealed();
    }
}