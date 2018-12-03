package talkapp.org.talkappmobile.activity.presenter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;

import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.WordSet;

@RunWith(MockitoJUnitRunner.class)
public class PracticeWordSetPresenterRightAnswerUnTouchedNPETest {
    @Mock
    private TextUtils textUtils;
    @Mock
    private StudyingPracticeWordSetInteractor interactor;
    @Mock
    private PracticeWordSetPresenterCurrentState state;
    @Mock
    private PracticeWordSetViewHideNewWordOnlyStrategy newWordOnlyStrategy;
    @Mock
    private PracticeWordSetViewHideAllStrategy hideAllStrategy;
    private PracticeWordSetPresenter presenter;

    @BeforeClass
    public static void setUpContext() {
        DIContextUtils.init(new TalkappMobileApplication());
    }

    @Before
    public void setUp() {
        presenter = new PracticeWordSetPresenter(new WordSet(), interactor, newWordOnlyStrategy, hideAllStrategy);
        PracticeWordSetViewStrategy strategy = Whitebox.getInternalState(presenter, "viewStrategy");
        Whitebox.setInternalState(strategy, "textUtils", textUtils);
        Whitebox.setInternalState(presenter, "state", state);
    }

    @Test
    public void rightAnswerTouched_whenSentenceIsNotInitializedYetNPE() {
        presenter.rightAnswerTouched();
    }
}