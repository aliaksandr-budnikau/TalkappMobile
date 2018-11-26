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
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.module.AndroidModule;
import talkapp.org.talkappmobile.module.BackEndServiceModule;
import talkapp.org.talkappmobile.module.BackEndServiceModule_;
import talkapp.org.talkappmobile.module.DaggerTestDIContext;
import talkapp.org.talkappmobile.module.GameplayModule_;
import talkapp.org.talkappmobile.module.InfraModule;
import talkapp.org.talkappmobile.module.ItemsListModule;
import talkapp.org.talkappmobile.module.LanguageModule_;
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
    PracticeWordSetExerciseService exerciseService;
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
    private static TalkappMobileApplication application = new TalkappMobileApplication();

    public  ClassForInjection(BackEndServiceModule backEndServiceModule) {
        TestDIContext context = DaggerTestDIContext.builder()
                .databaseModule(new TestDatabaseModule())
                .androidModule(new AndroidModule(application))
                .audioModule(new TestAudioModule())
                .gameplayModule(GameplayModule_.getInstance_(application))
                .dataModule(new TestDataModule())
                .infraModule(new InfraModule())
                .languageModule(LanguageModule_.getInstance_(application))
                .backEndServiceModule(backEndServiceModule)
                .itemsListModule(new ItemsListModule())
                .build();

        context.inject(this);
    }

    public ClassForInjection() {
        this(BackEndServiceModule_.getInstance_(application));
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

    public PracticeWordSetExerciseService getExerciseService() {
        return exerciseService;
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
