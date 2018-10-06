package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.List;

import talkapp.org.talkappmobile.component.Word2SentenceCache;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter.SPEECH_TIMEOUT_MILLIS;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetPresenterTest {
    @Mock
    private PracticeWordSetInteractor interactor;
    @Mock
    private PracticeWordSetViewStrategy viewStrategy;
    @Mock
    private Word2SentenceCache word2SentenceCache;
    private PracticeWordSetPresenter presenter;
    private WordSet wordSet;
    private Sentence sentence;

    @Before
    public void setUp() {
        wordSet = new WordSet();
        wordSet.setId("dsfse3");
        wordSet.setExperience(new WordSetExperience());
        wordSet.getExperience().setId("sdfs");
        presenter = new PracticeWordSetPresenter(wordSet, null);

        Whitebox.setInternalState(presenter, "interactor", interactor);
        Whitebox.setInternalState(presenter, "viewStrategy", viewStrategy);
        Whitebox.setInternalState(presenter, "word2SentenceCache", word2SentenceCache);

        sentence = new Sentence();
        sentence.setId("dsfsd");
        Whitebox.setInternalState(presenter, "currentSentence", sentence);
    }

    @Test
    public void onInitialiseExperience() {
        // when
        presenter.onInitialiseExperience();

        // then
        verify(viewStrategy).onInitialiseExperience(wordSet.getExperience());
    }

    @Test
    public void onSentencesFound() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setTranslations(new HashMap<String, String>());
        sentence.getTranslations().put("russian", "fsdfsfs");

        String word = "word";

        // when
        presenter.onSentencesFound(sentence, word);

        // then
        assertEquals(sentence, Whitebox.getInternalState(presenter, "currentSentence"));
        verify(viewStrategy).onSentencesFound(sentence, word);
        verify(word2SentenceCache).save(word, sentence);
    }

    @Test
    public void onAnswerEmpty() {
        presenter.onAnswerEmpty();
        verify(viewStrategy).onAnswerEmpty();
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
        presenter.onSpellingOrGrammarError(errors);

        // then
        verify(viewStrategy).onSpellingOrGrammarError(errors);
    }

    @Test
    public void onAccuracyTooLowError() {
        presenter.onAccuracyTooLowError();
        verify(viewStrategy).onAccuracyTooLowError();
    }

    @Test
    public void onUpdateProgress() {
        // setup
        int currentTrainingExperience = 32;

        // when
        presenter.onUpdateProgress(currentTrainingExperience);

        // then
        verify(viewStrategy).onUpdateProgress(wordSet.getExperience(), currentTrainingExperience);
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

        Whitebox.setInternalState(presenter, "currentSentence", sentence);

        // when
        presenter.onRightAnswer();

        // then
        verify(viewStrategy).onRightAnswer(sentence);
    }

    @Test
    public void onRecogniseVoiceButtonClick() {
        // when
        presenter.onRecogniseVoiceButtonClick();

        // then
        verify(interactor).recVoice(SPEECH_TIMEOUT_MILLIS, presenter);
        verify(interactor).recognizeVoice(presenter);
    }

    @Test
    public void onResume() {
        // setup
        String word = "word";
        wordSet.setWords(asList(word, "dsfsddsd"));

        // when
        presenter.onResume();

        // then
        verify(interactor).initialiseExperience(wordSet, presenter);
        verify(interactor).initialiseWordsSequence(wordSet, presenter);
    }

    @Test
    public void onDestroy() {
        presenter.onDestroy();
        assertNull(Whitebox.getInternalState(presenter, "viewStrategy"));
    }

    @Test
    public void onNextButtonClick() {
        // setup
        String word1 = "sdfsd";
        wordSet.setWords(asList(word1, "dsfsddsd"));
        Whitebox.setInternalState(presenter, "wordSequenceIterator", wordSet.getWords().listIterator());

        // when
        presenter.onNextButtonClick();

        // then
        verify(interactor).initialiseSentence(word1, presenter);
    }

    @Test
    public void onCheckAnswerButtonClick() {
        // setup
        String answer = "sdfsd";

        Sentence sentence = new Sentence();
        sentence.setId("323");

        // when
        Whitebox.setInternalState(presenter, "currentSentence", sentence);
        presenter.onCheckAnswerButtonClick(answer);

        // then
        verify(interactor).checkAnswer(answer, wordSet, sentence, presenter);
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
        presenter.onPlayVoiceButtonClick();
        verify(interactor).playVoice(presenter);
    }

    @Test
    public void rightAnswerTouched() {
        presenter.rightAnswerTouched();
        verify(viewStrategy).rightAnswerTouched(sentence);
    }

    @Test
    public void rightAnswerUntouched() {
        // setup
        String word = "word";

        Whitebox.setInternalState(presenter, "currentWord", word);

        // when
        presenter.rightAnswerUntouched();

        // then
        verify(viewStrategy).rightAnswerUntouched(sentence, word);
    }
}