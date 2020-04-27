package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import talkapp.org.talkappmobile.activity.CapitalLetterInNewWordTest;
import talkapp.org.talkappmobile.activity.ChangeSentenceTest;
import talkapp.org.talkappmobile.activity.MainActivityTest;
import talkapp.org.talkappmobile.activity.PracticeWordSetVocabularyFragmentTest;
import talkapp.org.talkappmobile.activity.WordSetsListFragmentTest;
import talkapp.org.talkappmobile.activity.interactor.AddingNewWordSetInteractorTest;
import talkapp.org.talkappmobile.activity.presenter.AddingNewWordSetPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenterAndInteractorForExpressionsIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetRepetitionPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.TopicsFragmentPresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.activity.presenter.WordSetsListPresenterAndInteractorIntegTest;

@Suite.SuiteClasses({
        WordSetsListPresenterAndInteractorIntegTest.class,
        PracticeWordSetRepetitionPresenterAndInteractorIntegTest.class,
        PracticeWordSetVocabularyPresenterAndInteractorIntegTest.class,
        PracticeWordSetPresenterAndInteractorIntegTest.class,
        PracticeWordSetPresenterAndInteractorForExpressionsIntegTest.class,
        AddingNewWordSetPresenterAndInteractorIntegTest.class,
        TopicsFragmentPresenterAndInteractorIntegTest.class,
        CapitalLetterInNewWordTest.class,
        WordSetsListFragmentTest.class,
        ChangeSentenceTest.class,
        MainActivityTest.class,
        PracticeWordSetVocabularyFragmentTest.class,
        AddingNewWordSetInteractorTest.class,
})
@RunWith(Suite.class)
public class SuiteDepOnExternalServer {
}
