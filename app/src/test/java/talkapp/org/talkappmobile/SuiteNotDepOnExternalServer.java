package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import talkapp.org.talkappmobile.activity.AddingNewWordSetFragmentTest;
import talkapp.org.talkappmobile.activity.custom.interactor.PronounceRightAnswerButtonInteractorTest;
import talkapp.org.talkappmobile.activity.custom.interactor.RightAnswerTextViewInteractorTest;
import talkapp.org.talkappmobile.activity.custom.presenter.RightAnswerTextViewPresenterTest;
import talkapp.org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractorTest;
import talkapp.org.talkappmobile.activity.interactor.StatisticActivityInteractorTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenterTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategyTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenterTest;
import talkapp.org.talkappmobile.activity.presenter.StudyingPracticeWordSetInteractorTest;
import talkapp.org.talkappmobile.activity.presenter.StudyingWordSetsListInteractorTest;
import talkapp.org.talkappmobile.activity.presenter.TopicsFragmentPresenterTest;
import talkapp.org.talkappmobile.activity.presenter.WordSetsListPresenterTest;
import talkapp.org.talkappmobile.component.impl.ExceptionHandlerTest;
import talkapp.org.talkappmobile.component.impl.SpeakerBeanTest;
import talkapp.org.talkappmobile.controller.AddingNewWordSetFragmentControllerIntegTest;
import talkapp.org.talkappmobile.service.impl.DataServerImplTest;
import talkapp.org.talkappmobile.service.impl.EqualityScorerBeanTest;
import talkapp.org.talkappmobile.service.impl.RefereeServiceImplTest;
import talkapp.org.talkappmobile.service.impl.RequestExecutorTest;
import talkapp.org.talkappmobile.service.impl.SentenceServiceImplTest;
import talkapp.org.talkappmobile.service.impl.TextUtilsImplTest;
import talkapp.org.talkappmobile.service.impl.UserExpServiceImplTest;
import talkapp.org.talkappmobile.service.impl.WordRepetitionProgressServiceImplTest;
import talkapp.org.talkappmobile.service.impl.WordSetServiceImplTest;

@Suite.SuiteClasses({
        WordSetsListPresenterTest.class,
        StudyingPracticeWordSetInteractorTest.class,
        StudyingWordSetsListInteractorTest.class,
        RightAnswerTextViewInteractorTest.class,
        ExceptionHandlerTest.class,
        SpeakerBeanTest.class,
        PracticeWordSetPresenterTest.class,
        PracticeWordSetViewStrategyTest.class,
        TopicsFragmentPresenterTest.class,
        PracticeWordSetVocabularyPresenterTest.class,
        PronounceRightAnswerButtonInteractorTest.class,
        RightAnswerTextViewPresenterTest.class,
        MainActivityDefaultFragmentInteractorTest.class,
        RefereeServiceImplTest.class,
        SentenceServiceImplTest.class,
        EqualityScorerBeanTest.class,
        TextUtilsImplTest.class,
        WordRepetitionProgressServiceImplTest.class,
        WordSetServiceImplTest.class,
        DataServerImplTest.class,
        RequestExecutorTest.class,
        AddingNewWordSetFragmentTest.class,
        UserExpServiceImplTest.class,
        StatisticActivityInteractorTest.class,
        AddingNewWordSetFragmentControllerIntegTest.class
})
@RunWith(Suite.class)
public class SuiteNotDepOnExternalServer {
}
