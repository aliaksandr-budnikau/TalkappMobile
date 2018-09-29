package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.List;

import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetPresenterTest {
    @Mock
    WordSetExperienceUtils experienceUtils;
    @Mock
    private PracticeWordSetView view;
    @Mock
    private PracticeWordSetInteractor interactor;
    @Mock
    private TextUtils textUtils;
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
        Whitebox.setInternalState(presenter, "textUtils", textUtils);
        Whitebox.setInternalState(presenter, "experienceUtils", experienceUtils);
    }

    @Test
    public void onInitialiseExperience() {
        // setup
        int progress = 32;
        WordSetExperience exp = wordSet.getExperience();

        // when
        when(experienceUtils.getProgress(exp.getTrainingExperience(), exp.getMaxTrainingExperience())).thenReturn(progress);
        presenter.onInitialiseExperience();

        // then
        verify(view).setProgress(progress);
    }

    @Test
    public void onSentencesFound() {
        // setup
        String origText = "fsdfsfs";

        Sentence sentence = new Sentence();
        sentence.setTranslations(new HashMap<String, String>());
        sentence.getTranslations().put("russian", origText);

        // when
        presenter.onSentencesFound(sentence);

        // then
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(origText);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).setAnswerText("");
    }

    @Test
    public void onAnswerEmpty() {
        presenter.onAnswerEmpty();
        verify(view).showMessageAnswerEmpty();
    }

    @Test
    public void onSpellingOrGrammarError() {
        // setup
        GrammarError error1 = new GrammarError();
        error1.setMessage("error1");

        GrammarError error2 = new GrammarError();
        error2.setMessage("error2");

        List<GrammarError> errors = asList(error1, error2);

        // when
        when(textUtils.buildSpellingGrammarErrorMessage(error1)).thenReturn("error1");
        when(textUtils.buildSpellingGrammarErrorMessage(error2)).thenReturn("error2");
        presenter.onSpellingOrGrammarError(errors);

        // then
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).showSpellingOrGrammarErrorPanel(error1.getMessage());
        verify(view).showSpellingOrGrammarErrorPanel(error2.getMessage());
    }

    @Test
    public void onAccuracyTooLowError() {
        presenter.onAccuracyTooLowError();
        verify(view).showMessageAccuracyTooLow();
        verify(view).hideSpellingOrGrammarErrorPanel();
    }

    @Test
    public void onUpdateProgress() {
        // setup
        int currentTrainingExperience = 32;
        int progress = 232;

        // when
        when(experienceUtils.getProgress(currentTrainingExperience, wordSet.getExperience().getMaxTrainingExperience())).thenReturn(progress);
        presenter.onUpdateProgress(currentTrainingExperience);

        // then
        verify(view).setProgress(progress);
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
        verify(view).setRightAnswer(sentence.getText());
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
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
        verify(view).setRightAnswer(sentence.getText());
    }

    @Test
    public void rightAnswerUntouched() {
        // setup
        String hiddenRightAnswer = "sdfsdf";

        // when
        when(textUtils.screenTextWith(sentence.getText())).thenReturn(hiddenRightAnswer);
        presenter.rightAnswerUntouched();

        // then
        verify(view).setRightAnswer(hiddenRightAnswer);
    }
}