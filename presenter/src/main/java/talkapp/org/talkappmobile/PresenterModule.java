package talkapp.org.talkappmobile;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.EqualityScorer;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceProvider;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.TextUtils;
import talkapp.org.talkappmobile.service.TopicService;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;

@Module
public class PresenterModule {
    private final ServiceFactory serviceFactory;

    public PresenterModule(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Provides
    @Singleton
    public ServiceFactory serviceFactory() {
        return serviceFactory;
    }

    @Provides
    @Singleton
    public WordRepetitionProgressService wordRepetitionProgressService(ServiceFactory serviceFactory) {
        return serviceFactory.getWordRepetitionProgressService();
    }

    @Provides
    @Singleton
    public WordTranslationService wordTranslationService(ServiceFactory serviceFactory) {
        return serviceFactory.getWordTranslationService();
    }

    @Provides
    @Singleton
    public WordSetService wordSetService(ServiceFactory serviceFactory) {
        return serviceFactory.getWordSetService();
    }

    @Provides
    @Singleton
    public DataServer dataServer(ServiceFactory serviceFactory) {
        return serviceFactory.getDataServer();
    }

    @Provides
    @Singleton
    public CurrentPracticeStateService currentPracticeStateService(ServiceFactory serviceFactory) {
        return serviceFactory.getCurrentPracticeStateService();
    }

    @Provides
    @Singleton
    public EqualityScorer equalityScorer(ServiceFactory serviceFactory) {
        return serviceFactory.getEqualityScorer();
    }

    @Provides
    @Singleton
    public UserExpService userExpService(ServiceFactory serviceFactory) {
        return serviceFactory.getUserExpService();
    }

    @Provides
    @Singleton
    public TopicService topicService(ServiceFactory serviceFactory) {
        return serviceFactory.getTopicService();
    }

    @Provides
    @Singleton
    public TextUtils textUtils(ServiceFactory serviceFactory) {
        return serviceFactory.getTextUtils();
    }

    @Provides
    @Singleton
    public RefereeService refereeService(ServiceFactory serviceFactory) {
        return serviceFactory.getRefereeService();
    }

    @Provides
    @Singleton
    public Logger logger(ServiceFactory serviceFactory) {
        return serviceFactory.getLogger();
    }

    @Provides
    @Singleton
    public SentenceProvider sentenceProvider(ServiceFactory serviceFactory) {
        return serviceFactory.getSentenceProvider();
    }

    @Provides
    @Singleton
    public SentenceService sentenceService(ServiceFactory serviceFactory) {
        return serviceFactory.getSentenceService();
    }
}
