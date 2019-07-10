package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import talkapp.org.talkappmobile.activity.CapitalLetterInNewWordTest;
import talkapp.org.talkappmobile.activity.ChangeSentenceTest;
import talkapp.org.talkappmobile.activity.WordSetsListFragmentTest;
import talkapp.org.talkappmobile.activity.presenter.AddingNewWordSetPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetRepetitionPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.TopicsFragmentPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.WordSetsListPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.service.impl.DataServerImplIntegTest;
import talkapp.org.talkappmobile.service.impl.WordRepetitionProgressServiceImplIntegTest;

@Suite.SuiteClasses({
        WordSetsListPresenterAndInteractorIntegTest.class,
        PracticeWordSetRepetitionPresenterAndInteractorIntegTest.class,
        PracticeWordSetVocabularyPresenterAndInteractorIntegTest.class,
        PracticeWordSetPresenterAndInteractorIntegTest.class,
        AddingNewWordSetPresenterAndInteractorIntegTest.class,
        TopicsFragmentPresenterAndInteractorIntegTest.class,
        CapitalLetterInNewWordTest.class,
        WordSetsListFragmentTest.class,
        ChangeSentenceTest.class,
        DataServerImplIntegTest.class,
        WordRepetitionProgressServiceImplIntegTest.class
})
@RunWith(Suite.class)
public class SuiteDepOnExternalServer {
}
