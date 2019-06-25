package org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.talkappmobile.service.impl.DataServerImplTest;
import org.talkappmobile.service.impl.EqualityScorerBeanTest;
import org.talkappmobile.service.impl.RandomWordsCombinatorBeanTest;
import org.talkappmobile.service.impl.RefereeServiceImplTest;
import org.talkappmobile.service.impl.SentenceServiceImplTest;
import org.talkappmobile.service.impl.TextUtilsImplTest;
import org.talkappmobile.service.impl.WordRepetitionProgressServiceImplTest;

@Suite.SuiteClasses({
        RefereeServiceImplTest.class,
        RandomWordsCombinatorBeanTest.class,
        SentenceServiceImplTest.class,
        EqualityScorerBeanTest.class,
        TextUtilsImplTest.class,
        WordRepetitionProgressServiceImplTest.class,
        DataServerImplTest.class
})
@RunWith(Suite.class)
public class SuiteNotDepOnExternalServer {
}