package talkapp.org.talkappmobile.activity.custom.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import talkapp.org.talkappmobile.activity.custom.presenter.PronounceRightAnswerButtonPresenter;
import talkapp.org.talkappmobile.activity.custom.view.PronounceRightAnswerButtonView;
import talkapp.org.talkappmobile.component.Speaker;
import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PronounceRightAnswerButtonInteractorTest {
    @Mock
    private Speaker speaker;
    @Mock
    private PronounceRightAnswerButtonView view;
    private PronounceRightAnswerButtonPresenter presenter;

    @Before
    public void init() {
        PronounceRightAnswerButtonInteractor interactor = new PronounceRightAnswerButtonInteractor(speaker);
        presenter = new PronounceRightAnswerButtonPresenter(interactor, view);
        Sentence sentence = new Sentence();
        sentence.setText("dsd");
        Word2Tokens word = new Word2Tokens("dsfds54e3whggfdA", 3);
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