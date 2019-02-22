package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Suite.SuiteClasses({
        SuiteDepOnExternalServer.class,
        SuiteNotDepOnExternalServer.class
})
@RunWith(Suite.class)
public class AllTests {
}
