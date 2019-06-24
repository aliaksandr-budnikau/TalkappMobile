package org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.talkappmobile.dao.impl.SentenceDaoImplTest;
import org.talkappmobile.dao.impl.WordRepetitionProgressDaoImplTest;
import org.talkappmobile.dao.impl.WordSetDaoImplTest;

@Suite.SuiteClasses({
        WordRepetitionProgressDaoImplTest.class,
        WordSetDaoImplTest.class,
        SentenceDaoImplTest.class,
})
@RunWith(Suite.class)
public class SuiteNotDepOnExternalServer {
}