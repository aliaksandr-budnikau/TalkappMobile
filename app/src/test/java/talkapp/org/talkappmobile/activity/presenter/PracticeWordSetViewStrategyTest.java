package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.service.TextUtils;
import org.talkappmobile.service.WordSetExperienceUtils;

import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;

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

    @Test
    public void onInitialiseExperience() {
        // setup
        int progress = 32;

        WordSet wordSet = new WordSet();
        wordSet.setTrainingExperience(23332);
        wordSet.setWords(asList(new Word2Tokens("sds", "sds", wordSet.getId()), new Word2Tokens("sds", "sds", wordSet.getId())));


        // when
        when(experienceUtils.getProgress(wordSet.getTrainingExperience(), wordSet.getWords().size() * 2)).thenReturn(progress);
        when(experienceUtils.getMaxTrainingProgress(wordSet)).thenReturn(wordSet.getWords().size() * 2);
        strategy.onInitialiseExperience(wordSet);

        // then
        verify(view).setProgress(progress);
    }

    @Test
    public void onSentencesFound() {
        // when
        strategy.onSentencesFound();

        // then
        verify(view).hideNextButton();
        verify(view).showCheckButton();
        verify(view).setAnswerText("");
    }

    @Test
    public void onAnswerEmpty() {
        strategy.onAnswerEmpty();
        verify(view).showMessageAnswerEmpty();
    }

    @Test
    public void onAccuracyTooLowError() {
        strategy.onAccuracyTooLowError();
        verify(view).showMessageAccuracyTooLow();
    }

    @Test
    public void onUpdateProgress() {
        // setup
        int progress = 232;

        WordSet wordSet = new WordSet();
        wordSet.setWords(asList(new Word2Tokens("sds", "sds", wordSet.getId()), new Word2Tokens("sds", "sds", wordSet.getId())));
        wordSet.setTrainingExperience(23332);

        // when
        when(experienceUtils.getProgress(wordSet.getTrainingExperience(), wordSet.getWords().size() * 2)).thenReturn(progress);
        when(experienceUtils.getMaxTrainingProgress(wordSet)).thenReturn(wordSet.getWords().size() * 2);
        strategy.onUpdateProgress(wordSet);

        // then
        verify(view).setProgress(progress);
    }


    @Test
    public void onTrainingFinished() {
        strategy.onTrainingFinished();
        verify(view).showCongratulationMessage();
        verify(view).showCloseButton();
        verify(view).hideNextButton();
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
}