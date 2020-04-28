package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import talkapp.org.talkappmobile.activity.AddingNewWordSetFragmentTest;
import talkapp.org.talkappmobile.activity.WordSetsListFragmentMockTest;
import talkapp.org.talkappmobile.component.impl.ExceptionHandlerTest;
import talkapp.org.talkappmobile.component.impl.SpeakerBeanTest;

@Suite.SuiteClasses({
        ExceptionHandlerTest.class,
        SpeakerBeanTest.class,
        AddingNewWordSetFragmentTest.class,
        WordSetsListFragmentMockTest.class,
})
@RunWith(Suite.class)
public class SuiteNotDepOnExternalServer {
}
