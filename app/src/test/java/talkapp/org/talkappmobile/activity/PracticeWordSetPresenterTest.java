package talkapp.org.talkappmobile.activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetPresenterTest {
    @Mock
    private PracticeWordSetView view;
    @Mock
    private PracticeWordSetInteractor interactor;
    private PracticeWordSetPresenter presenter;
    private WordSet wordSet;
    private Sentence sentence;

    @Before
    public void setUp() {
        wordSet = new WordSet();
        wordSet.setId("dsfse3");
        wordSet.setExperience(new WordSetExperience());
        wordSet.getExperience().setId("sdfs");
        presenter = new PracticeWordSetPresenter(wordSet, view);

        Whitebox.setInternalState(presenter, "interactor", interactor);

        sentence = new Sentence();
        sentence.setId("dsfsd");
        Whitebox.setInternalState(presenter, "sentence", sentence);
    }

    @Test
    public void onInitialiseExperience() {
        presenter.onInitialiseExperience();
        verify(view).setProgress(wordSet.getExperience());
    }

    @Test
    public void onSentencesFound() {
        // setup
        Sentence sentence = new Sentence();

        // when
        presenter.onSentencesFound(sentence);

        // then
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(sentence);
        verify(view).setHiddenRightAnswer(sentence);
        verify(view).setAnswerText(sentence);
    }

    @Test
    public void onAnswerEmpty() {
        presenter.onAnswerEmpty();
        verify(view).showMessageAnswerEmpty();
    }

    @Test
    public void onSpellingOrGrammarError() {
        presenter.onSpellingOrGrammarError();
        verify(view).showMessageSpellingOrGrammarError();
    }

    @Test
    public void onAccuracyTooLowError() {
        presenter.onAccuracyTooLowError();
        verify(view).showMessageAccuracyTooLow();
    }

    @Test
    public void onUpdateProgress() {
        // setup
        int currentTrainingExperience = 32;

        // when
        presenter.onUpdateProgress(currentTrainingExperience);

        // then
        verify(view).updateProgress(wordSet.getExperience(), currentTrainingExperience);
    }


    @Test
    public void onTrainingFinished() {
        presenter.onTrainingFinished();
        verify(view).showCongratulationMessage();
        verify(view).closeActivity();
        verify(view).openAnotherActivity();
    }

    @Test
    public void onRightAnswer() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setId("dsfs");

        Whitebox.setInternalState(presenter, "sentence", sentence);

        // when
        presenter.onRightAnswer();

        // then
        verify(view).setRightAnswer(sentence);
        verify(view).showNextButton();
        verify(view).hideCheckButton();
    }

    @Test
    public void onResume() {
        presenter.onResume();
        verify(interactor).initialiseExperience(wordSet, presenter);
        verify(interactor).initialiseWordsSequence(wordSet, presenter);
    }

    @Test
    public void onDestroy() {
        presenter.onDestroy();
        assertNull(Whitebox.getInternalState(presenter, "view"));
    }

    @Test
    public void onNextButtonClick() {
        presenter.onNextButtonClick();
        verify(interactor).initialiseSentence(wordSet, presenter);
    }

    @Test
    public void onCheckAnswerButtonClick() {
        // setup
        String answer = "sdfsd";

        Sentence sentence = new Sentence();
        sentence.setId("323");

        // when
        Whitebox.setInternalState(presenter, "sentence", sentence);
        presenter.onCheckAnswerButtonClick(answer);

        // then
        verify(interactor).checkAnswer(answer, wordSet, sentence, presenter);
    }

    @Test
    public void onStartPlaying() {
        presenter.onStartPlaying();
        verify(view).setEnableVoiceRecButton(false);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);
    }

    @Test
    public void onStopPlaying() {
        presenter.onStopPlaying();
        verify(view).setEnableVoiceRecButton(true);
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
    }

    @Test
    public void onPlayVoiceButtonClick() {
        presenter.onPlayVoiceButtonClick();
        verify(interactor).playVoice(presenter);
    }

    @Test
    public void rightAnswerTouched() {
        presenter.rightAnswerTouched();
        verify(view).setRightAnswer(sentence);
        verify(view, times(0)).setHiddenRightAnswer(sentence);
    }

    @Test
    public void rightAnswerUntouched() {
        presenter.rightAnswerUntouched();
        verify(view).setHiddenRightAnswer(sentence);
        verify(view, times(0)).setRightAnswer(sentence);
    }
}