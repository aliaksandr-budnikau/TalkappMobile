package talkapp.org.talkappmobile.activity.presenter;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;

import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetPresenterTest {
    @Mock
    private StudyingPracticeWordSetInteractor interactor;
    @Mock
    private PracticeWordSetFirstCycleViewStrategy viewStrategy;
    @Mock
    private PracticeWordSetPresenterCurrentState state;
    private PracticeWordSetPresenter presenter;

    @Before
    public void setUp() {
        presenter = new PracticeWordSetPresenter(new WordSet(), interactor, viewStrategy);
        Whitebox.setInternalState(presenter, "state", state);
    }

    @Test
    public void onInitialiseExperience() {
        WordSet wordSet = new WordSet();

        // when
        presenter.onInitialiseExperience(wordSet);

        // then
        verify(viewStrategy).onInitialiseExperience(wordSet);
    }

    @Test
    public void onSentencesFound() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setTranslations(new HashMap<String, String>());
        sentence.getTranslations().put("russian", "fsdfsfs");

        Word2Tokens word = new Word2Tokens("word");

        // when
        presenter.onSentencesFound(sentence, word);

        // then
        verify(viewStrategy).onSentencesFound(sentence, word);
    }

    @Test
    public void onAnswerEmpty() {
        presenter.onAnswerEmpty();
        verify(viewStrategy).onAnswerEmpty();
    }

    @Test
    public void onAccuracyTooLowError() {
        presenter.onAccuracyTooLowError();
        verify(viewStrategy).onAccuracyTooLowError();
    }

    @Test
    public void onUpdateProgress() {
        // setup
        WordSet wordSet = new WordSet();

        // when
        presenter.onUpdateProgress(wordSet);

        // then
        verify(viewStrategy).onUpdateProgress(wordSet);
    }

    @Test
    public void onTrainingFinished() {
        presenter.onTrainingFinished();
        verify(viewStrategy).onTrainingFinished();
    }

    @Test
    public void onRightAnswer() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setId("dsfs");

        // when
        presenter.onRightAnswer(sentence);

        // then
        verify(viewStrategy).onRightAnswer(sentence);
    }

    @Test
    public void onResume() {
        // setup
        WordSet wordSet = new WordSet();

        // when
        when(state.getWordSet()).thenReturn(wordSet);
        presenter.initialise();

        // then
        verify(interactor).initialiseExperience(wordSet, presenter);
        verify(interactor).initialiseWordsSequence(wordSet, presenter);
    }

    @Test
    public void onNextButtonClick() {
        // setup
        Word2Tokens word1 = new Word2Tokens("sdfsd");
        int wordSetId = 3;

        // when
        when(interactor.peekAnyNewWordByWordSetId(wordSetId)).thenReturn(word1);
        when(state.getWordSetId()).thenReturn(wordSetId);
        presenter.nextButtonClick();

        // then
        verify(viewStrategy).onNextButtonStart();
        verify(viewStrategy).onNextButtonFinish();
        verify(interactor).initialiseSentence(word1, wordSetId, presenter);
    }

    @Test
    public void onNextButtonClick_exception() {
        // when
        doThrow(new RuntimeException()).when(viewStrategy).onNextButtonStart();
        try {
            presenter.nextButtonClick();
        } catch (Exception e) {
        }

        // then
        verify(viewStrategy).onNextButtonFinish();
    }

    @Test
    public void onCheckAnswerButtonClick() {
        // setup
        WordSet wordSet = new WordSet();
        wordSet.setId(3);
        String answer = "sdfsd";

        Sentence sentence = new Sentence();
        sentence.setId("323");

        // when
        when(state.getWordSet()).thenReturn(wordSet);
        when(state.getWordSetId()).thenReturn(wordSet.getId());
        when(interactor.getCurrentSentence(wordSet.getId())).thenReturn(sentence);
        presenter.checkAnswerButtonClick(answer);

        // then
        verify(viewStrategy).onCheckAnswerFinish();
        verify(viewStrategy).onCheckAnswerStart();
        verify(interactor).checkAnswer(answer, wordSet, sentence, false, presenter);
    }

    @Test
    public void onCheckAnswerButtonClick_exception() {
        // setup
        String answer = "sdfsd";

        // when
        doThrow(new RuntimeException()).when(viewStrategy).onCheckAnswerStart();
        try {
            presenter.checkAnswerButtonClick(answer);
        } catch (Exception e) {
        }

        // then
        verify(viewStrategy).onCheckAnswerFinish();
    }

    @Test
    public void onStartPlaying() {
        presenter.onStartPlaying();
        verify(viewStrategy).onStartPlaying();
    }

    @Test
    public void onStopPlaying() {
        presenter.onStopPlaying();
        verify(viewStrategy).onStopPlaying();
    }

    @Test
    public void onPlayVoiceButtonClick() {
        // setup
        Uri empty = Uri.EMPTY;

        // when
        when(state.getVoiceRecordUri()).thenReturn(empty);
        presenter.playVoiceButtonClick();

        // then
        verify(interactor).playVoice(empty, presenter);
    }
}