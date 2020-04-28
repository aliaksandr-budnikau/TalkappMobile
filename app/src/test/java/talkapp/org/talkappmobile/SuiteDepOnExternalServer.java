package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import talkapp.org.talkappmobile.activity.CapitalLetterInNewWordTest;
import talkapp.org.talkappmobile.activity.ChangeSentenceTest;
import talkapp.org.talkappmobile.activity.MainActivityTest;
import talkapp.org.talkappmobile.activity.PracticeWordSetVocabularyFragmentTest;
import talkapp.org.talkappmobile.activity.WordSetsListFragmentTest;

@Suite.SuiteClasses({
        CapitalLetterInNewWordTest.class,
        WordSetsListFragmentTest.class,
        ChangeSentenceTest.class,
        MainActivityTest.class,
        PracticeWordSetVocabularyFragmentTest.class,
})
@RunWith(Suite.class)
public class SuiteDepOnExternalServer {
}
