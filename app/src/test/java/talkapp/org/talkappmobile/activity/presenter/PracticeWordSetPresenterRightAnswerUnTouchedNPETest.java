package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetPresenterRightAnswerUnTouchedNPETest {
    @Mock
    PracticeWordSetExerciseRepository exerciseRepository;
    @Mock
    private TextUtils textUtils;
    @Mock
    private PracticeWordSetInteractor interactor;
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
        PracticeWordSetViewStrategy strategy = Whitebox.getInternalState(presenter, "viewStrategy");
        Whitebox.setInternalState(strategy, "textUtils", textUtils);
        Whitebox.setInternalState(presenter, "state", state);
        Whitebox.setInternalState(presenter, "interactor", interactor);
    }

    @Test
    public void rightAnswerTouched_whenSentenceIsNotInitializedYetNPE() {
        String wordSetId = "wordSetId";
        when(state.getWordSetId()).thenReturn(wordSetId);
        when(interactor.getCurrentSentence(wordSetId)).thenReturn(null);
        presenter.rightAnswerTouched();
    }

    @Test
    public void rightAnswerUntouched_whenSentenceIsNotInitializedYetNPE() {
        String wordSetId = "wordSetId";
        when(state.getWordSetId()).thenReturn(wordSetId);
        when(interactor.getCurrentSentence(wordSetId)).thenReturn(null);
        presenter.rightAnswerUntouched();
    }
}