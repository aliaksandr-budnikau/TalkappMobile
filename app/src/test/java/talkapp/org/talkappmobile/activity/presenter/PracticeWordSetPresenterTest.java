package talkapp.org.talkappmobile.activity.presenter;

import android.net.Uri;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.List;

import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.config.DIContext;
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
    private PracticeWordSetInteractor interactor;
    @Mock
    private PracticeWordSetViewStrategy viewStrategy;
    @Mock
    private PracticeWordSetExerciseRepository practiceWordSetExerciseRepository;
    @Mock
    private PracticeWordSetPresenterCurrentState state;
    private PracticeWordSetPresenter presenter;

    @BeforeClass
    public static void setUpContext() {
        DIContext.init(new TalkappMobileApplication());
    }

    @Before
    public void setUp() {
        presenter = new PracticeWordSetPresenter(new WordSet(), null);

        Whitebox.setInternalState(presenter, "interactor", interactor);
        Whitebox.setInternalState(presenter, "viewStrategy", viewStrategy);
        Whitebox.setInternalState(presenter, "practiceWordSetExerciseRepository", practiceWordSetExerciseRepository);
        Whitebox.setInternalState(presenter, "state", state);
    }

    @Test
    public void onInitialiseExperience() {
        WordSetExperience exp = new WordSetExperience();

        // when
        presenter.onInitialiseExperience(exp);

        // then
        verify(viewStrategy).onInitialiseExperience(exp);
    }

    @Test
    public void onSentencesFound() {
        // setup
        Sentence sentence = new Sentence();
        sentence.setTranslations(new HashMap<String, String>());
        sentence.getTranslations().put("russian", "fsdfsfs");

        String word = "word";
        String wordSetId = "wordSetId";

        // when
        when(state.getWordSetId()).thenReturn(wordSetId);
        presenter.onSentencesFound(sentence, word);

        // then
        verify(state).setSentence(sentence);
        verify(viewStrategy).onSentencesFound(sentence, word);
        verify(practiceWordSetExerciseRepository).save(word, wordSetId, sentence);
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
        WordSetExperience wordSetExperience = new WordSetExperience();

        // when
        presenter.onUpdateProgress(wordSetExperience);

        // then
        verify(viewStrategy).onUpdateProgress(wordSetExperience);
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
        when(state.getSentence()).thenReturn(sentence);
        presenter.onRightAnswer();

        // then
        verify(viewStrategy).onRightAnswer(sentence);
    }

    @Test
    public void onResume() {
        // setup
        WordSet wordSet = new WordSet();

        // when
        when(state.getWordSet()).thenReturn(wordSet);
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
        String wordSetId = "sdfsdId";

        // when
        when(state.getWord()).thenReturn(word1);
        when(state.getWordSetId()).thenReturn(wordSetId);
        presenter.onNextButtonClick();

        // then
        verify(state).nextWord();
        verify(interactor).initialiseSentence(word1, wordSetId, presenter);
    }

    @Test
    public void onCheckAnswerButtonClick() {
        // setup
        WordSet wordSet = new WordSet();

        String answer = "sdfsd";

        Sentence sentence = new Sentence();
        sentence.setId("323");

        // when
        when(state.getWordSet()).thenReturn(wordSet);
        when(state.getSentence()).thenReturn(sentence);
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
        // setup
        Uri empty = Uri.EMPTY;

        // when
        when(state.getVoiceRecordUri()).thenReturn(empty);
        presenter.onPlayVoiceButtonClick();

        // then
        verify(interactor).playVoice(empty, presenter);
    }

    @Test
    public void rightAnswerTouched() {
        // setup
        Sentence sentence = new Sentence();

        // when
        when(state.getSentence()).thenReturn(sentence);
        presenter.rightAnswerTouched();

        // then
        verify(viewStrategy).rightAnswerTouched(sentence);
    }

    @Test
    public void rightAnswerUntouched() {
        // setup
        Sentence sentence = new Sentence();
        String word = "word";

        // when
        when(state.getSentence()).thenReturn(sentence);
        when(state.getWord()).thenReturn(word);
        presenter.rightAnswerUntouched();

        // then
        verify(viewStrategy).rightAnswerUntouched(sentence, word);
    }
}