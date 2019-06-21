package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import talkapp.org.talkappmobile.activity.custom.interactor.PronounceRightAnswerButtonInteractorTest;
import talkapp.org.talkappmobile.activity.custom.interactor.RightAnswerTextViewInteractorTest;
import talkapp.org.talkappmobile.activity.custom.presenter.RightAnswerTextViewPresenterTest;
import talkapp.org.talkappmobile.activity.custom.presenter.WordSetListAdapterPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractorTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenterTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategyTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenterTest;
import talkapp.org.talkappmobile.activity.presenter.StudyingPracticeWordSetInteractorTest;
import talkapp.org.talkappmobile.activity.presenter.StudyingWordSetsListInteractorTest;
import talkapp.org.talkappmobile.activity.presenter.TopicsFragmentPresenterTest;
import talkapp.org.talkappmobile.activity.presenter.WordSetsListPresenterTest;
import talkapp.org.talkappmobile.component.backend.impl.DataServerImplTest;
import talkapp.org.talkappmobile.component.database.dao.impl.WordRepetitionProgressDaoImplTest;
import talkapp.org.talkappmobile.component.database.dao.impl.WordSetDaoImplTest;
import talkapp.org.talkappmobile.component.database.dao.impl.local.SentenceDaoImplTest;
import talkapp.org.talkappmobile.component.database.impl.WordRepetitionProgressServiceImplTest;
import talkapp.org.talkappmobile.component.impl.EqualityScorerBeanTest;
import talkapp.org.talkappmobile.component.impl.ExceptionHandlerTest;
import talkapp.org.talkappmobile.component.impl.LoggerBeanTest;
import talkapp.org.talkappmobile.component.impl.RandomSentenceSelectorBeanTest;
import talkapp.org.talkappmobile.component.impl.RandomWordsCombinatorBeanTest;
import talkapp.org.talkappmobile.component.impl.RefereeServiceImplTest;
import talkapp.org.talkappmobile.component.impl.SentenceServiceImplTest;
import talkapp.org.talkappmobile.component.impl.SpeakerBeanTest;
import talkapp.org.talkappmobile.component.impl.TextUtilsImplTest;

@Suite.SuiteClasses({
        WordSetsListPresenterTest.class,
        RefereeServiceImplTest.class,
        StudyingPracticeWordSetInteractorTest.class,
        StudyingWordSetsListInteractorTest.class,
        RightAnswerTextViewInteractorTest.class,
        RandomWordsCombinatorBeanTest.class,
        ExceptionHandlerTest.class,
        SpeakerBeanTest.class,
        RandomSentenceSelectorBeanTest.class,
        PracticeWordSetPresenterTest.class,
        SentenceServiceImplTest.class,
        EqualityScorerBeanTest.class,
        TextUtilsImplTest.class,
        LoggerBeanTest.class,
        PracticeWordSetViewStrategyTest.class,
        WordRepetitionProgressServiceImplTest.class,
        TopicsFragmentPresenterTest.class,
        WordRepetitionProgressDaoImplTest.class,
        DataServerImplTest.class,
        ExampleUnitTest.class,
        PracticeWordSetVocabularyPresenterTest.class,
        PronounceRightAnswerButtonInteractorTest.class,
        RightAnswerTextViewPresenterTest.class,
        WordSetDaoImplTest.class,
        SentenceDaoImplTest.class,
        MainActivityDefaultFragmentInteractorTest.class,
        WordSetListAdapterPresenterAndInteractorIntegTest.class
})
@RunWith(Suite.class)
public class SuiteNotDepOnExternalServer {
}
