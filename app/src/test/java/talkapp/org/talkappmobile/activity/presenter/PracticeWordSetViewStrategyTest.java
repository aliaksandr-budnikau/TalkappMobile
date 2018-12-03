package talkapp.org.talkappmobile.activity.presenter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
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
    @InjectMocks
    private PracticeWordSetViewStrategy strategy;

    @BeforeClass
    public static void setUpContext() {
        DIContextUtils.init(new TalkappMobileApplication());
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

        Word2Tokens word = new Word2Tokens("word");

        // when
        strategy.onSentencesFound(sentence, word);

        // then
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setOriginalText(origText);
        verify(view).setRightAnswerModel(sentence, word);
        verify(view).maskRightAnswerEntirely();
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
        int progress = 232;

        WordSetExperience exp = new WordSetExperience();
        exp.setMaxTrainingExperience(2332);
        exp.setTrainingExperience(23332);

        // when
        when(experienceUtils.getProgress(exp.getTrainingExperience(), exp.getMaxTrainingExperience())).thenReturn(progress);
        strategy.onUpdateProgress(exp);

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
        // when
        strategy.rightAnswerTouched();

        // then
        verify(view).unmaskRightAnswer();
    }

    @Test
    public void rightAnswerUntouched() {
        // when
        strategy.rightAnswerUntouched();

        // then
        verify(view).maskRightAnswerEntirely();
    }
}