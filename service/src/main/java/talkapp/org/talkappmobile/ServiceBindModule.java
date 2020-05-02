package talkapp.org.talkappmobile;

import dagger.Binds;
import dagger.Module;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.CurrentPracticeStateServiceImpl;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.DataServerImpl;
import talkapp.org.talkappmobile.service.EqualityScorer;
import talkapp.org.talkappmobile.service.EqualityScorerImpl;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.LoggerImpl;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.RefereeServiceImpl;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.SentenceServiceImpl;
import talkapp.org.talkappmobile.service.TextUtils;
import talkapp.org.talkappmobile.service.TextUtilsImpl;
import talkapp.org.talkappmobile.service.TopicService;
import talkapp.org.talkappmobile.service.TopicServiceImpl;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.UserExpServiceImpl;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressServiceImpl;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.WordTranslationServiceImpl;

@Module
public abstract class ServiceBindModule {

    @Binds
    abstract DataServer bindDataServer(DataServerImpl target);

    @Binds
    abstract EqualityScorer bindEqualityScorer(EqualityScorerImpl target);

    @Binds
    abstract WordRepetitionProgressService bindWordRepetitionProgressService(WordRepetitionProgressServiceImpl target);

    @Binds
    abstract UserExpService bindUserExpService(UserExpServiceImpl target);

    @Binds
    abstract TopicService bindTopicService(TopicServiceImpl target);

    @Binds
    abstract WordTranslationService bindWordTranslationService(WordTranslationServiceImpl target);

    @Binds
    abstract SentenceService bindSentenceService(SentenceServiceImpl target);

    @Binds
    abstract RefereeService bindRefereeService(RefereeServiceImpl target);

    @Binds
    abstract CurrentPracticeStateService bindCurrentPracticeStateService(CurrentPracticeStateServiceImpl target);

    @Binds
    abstract TextUtils bindTextUtils(TextUtilsImpl target);

    @Binds
    abstract Logger bindLogger(LoggerImpl target);
}