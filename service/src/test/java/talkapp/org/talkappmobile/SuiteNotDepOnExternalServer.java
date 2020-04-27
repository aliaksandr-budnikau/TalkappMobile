package talkapp.org.talkappmobile;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import talkapp.org.talkappmobile.service.impl.DataServerImplTest;
import talkapp.org.talkappmobile.service.impl.EqualityScorerImplTest;
import talkapp.org.talkappmobile.service.impl.RefereeServiceImplTest;
import talkapp.org.talkappmobile.service.impl.RequestExecutorTest;
import talkapp.org.talkappmobile.service.impl.SentenceServiceImplTest;
import talkapp.org.talkappmobile.service.impl.TextUtilsImplTest;
import talkapp.org.talkappmobile.service.impl.UserExpServiceImplTest;
import talkapp.org.talkappmobile.service.impl.WordRepetitionProgressServiceImplTest;
import talkapp.org.talkappmobile.service.impl.WordSetServiceImplTest;
import talkapp.org.talkappmobile.service.impl.WordTranslationSentenceProviderDecoratorTest;

@Suite.SuiteClasses({
        RefereeServiceImplTest.class,
        SentenceServiceImplTest.class,
        EqualityScorerImplTest.class,
        TextUtilsImplTest.class,
        WordRepetitionProgressServiceImplTest.class,
        WordTranslationSentenceProviderDecoratorTest.class,
        WordSetServiceImplTest.class,
        DataServerImplTest.class,
        RequestExecutorTest.class,
        UserExpServiceImplTest.class,
})
@RunWith(Suite.class)
public class SuiteNotDepOnExternalServer {
}
