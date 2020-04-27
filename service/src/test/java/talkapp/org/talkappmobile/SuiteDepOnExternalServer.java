package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import talkapp.org.talkappmobile.service.impl.DataServerImplIntegTest;
import talkapp.org.talkappmobile.service.impl.UserExpServiceImplIntegTest;
import talkapp.org.talkappmobile.service.impl.WordRepetitionProgressServiceImplIntegTest;

@Suite.SuiteClasses({
        DataServerImplIntegTest.class,
        UserExpServiceImplIntegTest.class,
        WordRepetitionProgressServiceImplIntegTest.class
})
@RunWith(Suite.class)
public class SuiteDepOnExternalServer {
}
