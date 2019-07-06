package org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.talkappmobile.activity.custom.interactor.PronounceRightAnswerButtonInteractorTest;
import org.talkappmobile.activity.custom.interactor.RightAnswerTextViewInteractorTest;
import org.talkappmobile.activity.custom.presenter.RightAnswerTextViewPresenterTest;
import org.talkappmobile.activity.custom.presenter.WordSetListAdapterPresenterAndInteractorIntegTest;
import org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractorTest;
import org.talkappmobile.activity.presenter.PracticeWordSetPresenterTest;
import org.talkappmobile.activity.presenter.PracticeWordSetViewStrategyTest;
import org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenterTest;
import org.talkappmobile.activity.presenter.StudyingPracticeWordSetInteractorTest;
import org.talkappmobile.activity.presenter.StudyingWordSetsListInteractorTest;
import org.talkappmobile.activity.presenter.TopicsFragmentPresenterTest;
import org.talkappmobile.activity.presenter.WordSetsListPresenterTest;
import org.talkappmobile.component.impl.ExceptionHandlerTest;
import org.talkappmobile.component.impl.SpeakerBeanTest;
import org.talkappmobile.dao.impl.SentenceDaoImplTest;
import org.talkappmobile.dao.impl.WordRepetitionProgressDaoImplTest;
import org.talkappmobile.dao.impl.WordSetDaoImplTest;
import org.talkappmobile.service.impl.DataServerImplTest;
import org.talkappmobile.service.impl.EqualityScorerBeanTest;
import org.talkappmobile.service.impl.RandomWordsCombinatorBeanTest;
import org.talkappmobile.service.impl.RefereeServiceImplTest;
import org.talkappmobile.service.impl.SentenceServiceImplTest;
import org.talkappmobile.service.impl.TextUtilsImplTest;
import org.talkappmobile.service.impl.WordRepetitionProgressServiceImplTest;
import org.talkappmobile.service.impl.WordSetServiceImplTest;

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
        WordSetListAdapterPresenterAndInteractorIntegTest.class,
        RefereeServiceImplTest.class,
        RandomWordsCombinatorBeanTest.class,
        SentenceServiceImplTest.class,
        EqualityScorerBeanTest.class,
        TextUtilsImplTest.class,
        WordRepetitionProgressServiceImplTest.class,
        WordSetServiceImplTest.class,
        DataServerImplTest.class,
        WordRepetitionProgressDaoImplTest.class,
        WordSetDaoImplTest.class,
        SentenceDaoImplTest.class,
})
@RunWith(Suite.class)
public class SuiteNotDepOnExternalServer {
}
