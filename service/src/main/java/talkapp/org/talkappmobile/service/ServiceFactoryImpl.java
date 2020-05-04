package talkapp.org.talkappmobile.service;

import android.content.Context;

import javax.inject.Inject;

import talkapp.org.talkappmobile.DaggerServiceComponent;
import talkapp.org.talkappmobile.ServiceComponent;
import talkapp.org.talkappmobile.ServiceModule;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryProvider;

public class ServiceFactoryImpl implements ServiceFactory {

    @Inject
    SentenceProvider sentenceProvider;
    @Inject
    EqualityScorer equalityScorer;
    @Inject
    WordSetService wordSetService;
    @Inject
    WordRepetitionProgressService wordRepetitionProgressService;
    @Inject
    UserExpService userExpService;
    @Inject
    TopicService topicService;
    @Inject
    WordTranslationService wordTranslationService;
    @Inject
    SentenceService sentenceService;
    @Inject
    RefereeService refereeService;
    @Inject
    CurrentPracticeStateService currentPracticeStateService;
    @Inject
    DataServer dataServer;
    @Inject
    TextUtils textUtils;
    @Inject
    Logger logger;

    public ServiceFactoryImpl(Context context) {
        this(RepositoryFactoryProvider.get(context));
    }

    public ServiceFactoryImpl(RepositoryFactory repositoryFactory) {
        ServiceComponent component = DaggerServiceComponent.builder()
                .serviceModule(new ServiceModule(repositoryFactory))
                .build();
        component.inject(this);
    }

    @Override
    public EqualityScorer getEqualityScorer() {
        return equalityScorer;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public WordSetService getWordSetService() {
        return wordSetService;
    }

    @Override
    public WordRepetitionProgressService getWordRepetitionProgressService() {
        return wordRepetitionProgressService;
    }

    @Override
    public UserExpService getUserExpService() {
        return userExpService;
    }

    @Override
    public TopicService getTopicService() {
        return topicService;
    }

    @Override
    public WordTranslationService getWordTranslationService() {
        return wordTranslationService;
    }

    @Override
    public CurrentPracticeStateService getCurrentPracticeStateService() {
        return currentPracticeStateService;
    }

    @Override
    public SentenceService getSentenceService() {
        return sentenceService;
    }

    @Override
    public RefereeService getRefereeService() {
        return refereeService;
    }

    @Override
    public DataServer getDataServer() {
        return dataServer;
    }

    @Override
    public SentenceProvider getSentenceProvider() {
        return sentenceProvider;
    }

    @Override
    public TextUtils getTextUtils() {
        return textUtils;
    }
}