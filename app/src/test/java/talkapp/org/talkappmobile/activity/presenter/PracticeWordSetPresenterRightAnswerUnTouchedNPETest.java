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
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.WordSet;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetPresenterRightAnswerUnTouchedNPETest {
    @Mock
    private TextUtils textUtils;
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
    }

    @Test
    public void rightAnswerTouched_whenSentenceIsNotInitializedYetNPE() {
        when(state.getSentence()).thenReturn(null);
        presenter.rightAnswerTouched();
    }

    @Test
    public void rightAnswerUntouched_whenSentenceIsNotInitializedYetNPE() {
        when(state.getSentence()).thenReturn(null);
        presenter.rightAnswerUntouched();
    }
}