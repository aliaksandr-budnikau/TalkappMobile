package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import talkapp.org.talkappmobile.activity.FragmentTests;
import talkapp.org.talkappmobile.activity.presenter.AddingNewWordSetPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetRepetitionPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.TopicsFragmentPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.WordSetsListPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.component.backend.impl.DataServerImplIntegTest;
import talkapp.org.talkappmobile.component.database.impl.WordRepetitionProgressServiceImplIntegTest;

@Suite.SuiteClasses({
        DataServerImplIntegTest.class,
        WordSetsListPresenterAndInteractorIntegTest.class,
        PracticeWordSetRepetitionPresenterAndInteractorIntegTest.class,
        PracticeWordSetVocabularyPresenterAndInteractorIntegTest.class,
        PracticeWordSetPresenterAndInteractorIntegTest.class,
        AddingNewWordSetPresenterAndInteractorIntegTest.class,
        TopicsFragmentPresenterAndInteractorIntegTest.class,
        WordRepetitionProgressServiceImplIntegTest.class,
        FragmentTests.class
})
@RunWith(Suite.class)
public class SuiteDepOnExternalServer {
}
