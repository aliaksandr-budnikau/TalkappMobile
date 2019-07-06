package org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import org.talkappmobile.activity.CapitalLetterInNewWordTest;
import org.talkappmobile.activity.ChangeSentenceTest;
import org.talkappmobile.activity.WordSetsListFragmentTest;
import org.talkappmobile.activity.presenter.AddingNewWordSetPresenterAndInteractorIntegTest;
import org.talkappmobile.activity.presenter.PracticeWordSetPresenterAndInteractorIntegTest;
import org.talkappmobile.activity.presenter.PracticeWordSetRepetitionPresenterAndInteractorIntegTest;
import org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenterAndInteractorIntegTest;
import org.talkappmobile.activity.presenter.TopicsFragmentPresenterAndInteractorIntegTest;
import org.talkappmobile.activity.presenter.WordSetsListPresenterAndInteractorIntegTest;
import org.talkappmobile.service.impl.DataServerImplIntegTest;
import org.talkappmobile.service.impl.WordRepetitionProgressServiceImplIntegTest;

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
