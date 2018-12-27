package talkapp.org.talkappmobile.activity.presenter;

import javax.inject.Inject;

import talkapp.org.talkappmobile.activity.interactor.LoginInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
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
import talkapp.org.talkappmobile.module.InfraModule_;
import talkapp.org.talkappmobile.module.LanguageModule_;
import talkapp.org.talkappmobile.module.TestAudioModule;
import talkapp.org.talkappmobile.module.TestDIContext;
import talkapp.org.talkappmobile.module.TestDatabaseModule;

public class ClassForInjection {
    private static TalkappMobileApplication application = new TalkappMobileApplication();
    @Inject
    StudyingPracticeWordSetInteractor studyingPracticeWordSetInteractor;
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
    StudyingWordSetsListInteractor studyingWordSetsListInteractor;
    @Inject
    WordSetExperienceDao wordSetExperienceDao;
    @Inject
    PracticeWordSetVocabularyInteractor practiceWordSetVocabularyInteractor;
    @Inject
    Speaker speaker;
    @Inject
    InfraComponentsFactory componentsFactory;
    @Inject
    TopicRestClient topicRestClient;

    public ClassForInjection(BackEndServiceModule backEndServiceModule) {
        TestDIContext context = DaggerTestDIContext.builder()
                .databaseModule(new TestDatabaseModule())
                .androidModule(new AndroidModule(application))
                .audioModule(new TestAudioModule())
                .gameplayModule(GameplayModule_.getInstance_(application))
                .infraModule(InfraModule_.getInstance_(application))
                .languageModule(LanguageModule_.getInstance_(application))
                .backEndServiceModule(backEndServiceModule)
                .build();

        context.inject(this);
    }

    public ClassForInjection() {
        this(BackEndServiceModule_.getInstance_(application));
    }

    public BackendServer getServer() {
        return server;
    }

    public StudyingPracticeWordSetInteractor getStudyingPracticeWordSetInteractor() {
        return studyingPracticeWordSetInteractor;
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

    public StudyingWordSetsListInteractor getStudyingWordSetsListInteractor() {
        return studyingWordSetsListInteractor;
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

    public TopicRestClient getTopicRestClient() {
        return topicRestClient;
    }
}
