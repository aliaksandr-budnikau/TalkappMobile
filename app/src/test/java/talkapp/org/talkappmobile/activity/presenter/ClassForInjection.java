package talkapp.org.talkappmobile.activity.presenter;

import javax.inject.Inject;

import talkapp.org.talkappmobile.activity.interactor.AllWordSetsInteractor;
import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.interactor.LoginInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.InfraComponentsFactory;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.ViewStrategyFactory;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.TopicRestClient;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.module.AndroidModule;
import talkapp.org.talkappmobile.module.BackEndServiceModule;
import talkapp.org.talkappmobile.module.ConcurrentModule;
import talkapp.org.talkappmobile.module.DaggerTestDIContext;
import talkapp.org.talkappmobile.module.GameplayModule;
import talkapp.org.talkappmobile.module.InfraModule;
import talkapp.org.talkappmobile.module.ItemsListModule;
import talkapp.org.talkappmobile.module.LanguageModule;
import talkapp.org.talkappmobile.module.TestAudioModule;
import talkapp.org.talkappmobile.module.TestDIContext;
import talkapp.org.talkappmobile.module.TestDataModule;
import talkapp.org.talkappmobile.module.TestDatabaseModule;

public class ClassForInjection {
    @Inject
    PracticeWordSetInteractor practiceWordSetInteractor;
    @Inject
    LoginInteractor loginInteractor;
    @Inject
    TopicsFragmentInteractor topicsFragmentInteractor;
    @Inject
    ViewStrategyFactory viewStrategyFactory;
    @Inject
    BackendServer server;
    @Inject
    PracticeWordSetExerciseRepository exerciseRepository;
    @Inject
    AllWordSetsInteractor allWordSetsInteractor;
    @Inject
    WordSetExperienceDao wordSetExperienceDao;
    @Inject
    PracticeWordSetVocabularyInteractor practiceWordSetVocabularyInteractor;
    @Inject
    Speaker speaker;
    @Inject
    InfraComponentsFactory componentsFactory;
    @Inject
    ExceptionHandlerInteractor exceptionHandlerInteractor;
    @Inject
    TopicRestClient topicRestClient;

    public ClassForInjection(BackEndServiceModule backEndServiceModule) {
        TestDIContext context = DaggerTestDIContext.builder()
                .databaseModule(new TestDatabaseModule())
                .androidModule(new AndroidModule(new TalkappMobileApplication()))
                .audioModule(new TestAudioModule())
                .gameplayModule(new GameplayModule())
                .concurrentModule(new ConcurrentModule())
                .dataModule(new TestDataModule())
                .infraModule(new InfraModule())
                .languageModule(new LanguageModule())
                .backEndServiceModule(backEndServiceModule)
                .itemsListModule(new ItemsListModule())
                .build();

        context.inject(this);
    }

    public ClassForInjection() {
        this(new BackEndServiceModule());
    }

    public BackendServer getServer() {
        return server;
    }

    public PracticeWordSetInteractor getPracticeWordSetInteractor() {
        return practiceWordSetInteractor;
    }

    public LoginInteractor getLoginInteractor() {
        return loginInteractor;
    }

    public ViewStrategyFactory getViewStrategyFactory() {
        return viewStrategyFactory;
    }

    public PracticeWordSetExerciseRepository getExerciseRepository() {
        return exerciseRepository;
    }

    public TopicsFragmentInteractor getTopicsFragmentInteractor() {
        return topicsFragmentInteractor;
    }

    public AllWordSetsInteractor getAllWordSetsInteractor() {
        return allWordSetsInteractor;
    }

    public WordSetExperienceDao getWordSetExperienceDao() {
        return wordSetExperienceDao;
    }

    public PracticeWordSetVocabularyInteractor getPracticeWordSetVocabularyInteractor() {
        return practiceWordSetVocabularyInteractor;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public InfraComponentsFactory getComponentsFactory() {
        return componentsFactory;
    }

    public ExceptionHandlerInteractor getExceptionHandlerInteractor() {
        return exceptionHandlerInteractor;
    }

    public TopicRestClient getTopicRestClient() {
        return topicRestClient;
    }
}
