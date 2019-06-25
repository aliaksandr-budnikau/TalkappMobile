package org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.talkappmobile.service.impl.DataServerImplIntegTest;
import org.talkappmobile.service.impl.WordRepetitionProgressServiceImplIntegTest;

@Suite.SuiteClasses({
        DataServerImplIntegTest.class,
        WordRepetitionProgressServiceImplIntegTest.class
})
@RunWith(Suite.class)
public class SuiteDepOnExternalServer {
}