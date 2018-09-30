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
import talkapp.org.talkappmobile.model.WordSetExperience;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetViewStrategyTest {
    @Mock
    WordSetExperienceUtils experienceUtils;
    @Mock
    private PracticeWordSetView view;
    @Mock
    private TextUtils textUtils;
    private PracticeWordSetViewStrategy strategy;

    @Before
    public void setUp() {
        strategy = new PracticeWordSetViewStrategy(view);
        Whitebox.setInternalState(strategy, "textUtils", textUtils);
        Whitebox.setInternalState(strategy, "experienceUtils", experienceUtils);
    }

    @Test
    public void onInitialiseExperience() {
        // setup
        int progress = 32;
        WordSetExperience exp = new WordSetExperience();
        exp.setMaxTrainingExperience(2332);
        exp.setTrainingExperience(23332);

        // when
        when(experienceUtils.getProgress(exp.getTrainingExperience(), exp.getMaxTrainingExperience())).thenReturn(progress);
        strategy.onInitialiseExperience(exp);

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
        strategy.onSentencesFound(sentence);

        // then
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(origText);
        verify(view).setRightAnswer(sentence.getText());
        verify(view).setAnswerText("");
    }

    @Test
    public void onAnswerEmpty() {
        strategy.onAnswerEmpty();
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
        strategy.onSpellingOrGrammarError(errors);

        // then
        verify(view).showMessageSpellingOrGrammarError();
        verify(view).showSpellingOrGrammarErrorPanel(error1.getMessage());
        verify(view).showSpellingOrGrammarErrorPanel(error2.getMessage());
    }

    @Test
    public void onAccuracyTooLowError() {
        strategy.onAccuracyTooLowError();
        verify(view).showMessageAccuracyTooLow();
        verify(view).hideSpellingOrGrammarErrorPanel();
    }

    @Test
    public void onUpdateProgress() {
        // setup
        int currentTrainingExperience = 32;
        int progress = 232;

        WordSetExperience exp = new WordSetExperience();
        exp.setMaxTrainingExperience(2332);
        exp.setTrainingExperience(23332);

        // when
        when(experienceUtils.getProgress(currentTrainingExperience, exp.getMaxTrainingExperience())).thenReturn(progress);
        strategy.onUpdateProgress(exp, currentTrainingExperience);

        // then
        verify(view).setProgress(progress);
    }


    @Test
    public void onTrainingFinished() {
        strategy.onTrainingFinished();
        verify(view).showCongratulationMessage();
        verify(view).closeActivity();
        verify(view).openAnotherActivity();
    }

    @Test
    public void onRightAnswer() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setId("dsfs");

        // when
        strategy.onRightAnswer(sentence);

        // then
        verify(view).setRightAnswer(sentence.getText());
        verify(view).showNextButton();
        verify(view).hideCheckButton();
        verify(view).hideSpellingOrGrammarErrorPanel();
    }

    @Test
    public void onStartPlaying() {
        strategy.onStartPlaying();
        verify(view).setEnableVoiceRecButton(false);
        verify(view).setEnableCheckButton(false);
        verify(view).setEnableNextButton(false);
    }

    @Test
    public void onStopPlaying() {
        strategy.onStopPlaying();
        verify(view).setEnableVoiceRecButton(true);
        verify(view).setEnableCheckButton(true);
        verify(view).setEnableNextButton(true);
    }

    @Test
    public void rightAnswerTouched() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setText("sdds");

        // when
        strategy.rightAnswerTouched(sentence);

        // then
        verify(view).setRightAnswer(sentence.getText());
    }

    @Test
    public void rightAnswerUntouched() {
        // setup
        String hiddenRightAnswer = "sdfsdf";
        Sentence sentence = new Sentence();
        sentence.setText("sdds");

        // when
        when(textUtils.screenTextWith(sentence.getText())).thenReturn(hiddenRightAnswer);
        strategy.rightAnswerUntouched(sentence);

        // then
        verify(view).setRightAnswer(hiddenRightAnswer);
    }
}