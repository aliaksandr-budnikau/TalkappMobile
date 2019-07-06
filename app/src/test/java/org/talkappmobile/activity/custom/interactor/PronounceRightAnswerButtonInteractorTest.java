package org.talkappmobile.activity.custom.interactor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

import org.talkappmobile.activity.custom.presenter.PronounceRightAnswerButtonPresenter;
import org.talkappmobile.activity.custom.view.PronounceRightAnswerButtonView;
import org.talkappmobile.component.Speaker;

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