package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.TopicsFragmentPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.WordSetsListPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.component.backend.impl.DataServerImplIntegTest;

@Suite.SuiteClasses({
        DataServerImplIntegTest.class,
        WordSetsListPresenterAndInteractorIntegTest.class,
        PracticeWordSetVocabularyPresenterAndInteractorIntegTest.class,
        PracticeWordSetPresenterAndInteractorIntegTest.class,
        TopicsFragmentPresenterAndInteractorIntegTest.class
})
@RunWith(Suite.class)
public class SuiteDepOnExternalServer {
}
