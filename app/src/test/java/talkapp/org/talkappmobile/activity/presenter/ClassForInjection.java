package talkapp.org.talkappmobile.activity.presenter;

import android.content.Context;

import javax.inject.Inject;

import talkapp.org.talkappmobile.activity.interactor.LoginInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.ViewStrategyFactory;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.module.AndroidModule;
import talkapp.org.talkappmobile.module.AudioModule;
import talkapp.org.talkappmobile.module.BackEndServiceModule;
import talkapp.org.talkappmobile.module.ConcurrentModule;
import talkapp.org.talkappmobile.module.DaggerTestDIContext;
import talkapp.org.talkappmobile.module.GameplayModule;
import talkapp.org.talkappmobile.module.InfraModule;
import talkapp.org.talkappmobile.module.ItemsListModule;
import talkapp.org.talkappmobile.module.LanguageModule;
import talkapp.org.talkappmobile.module.TestDIContext;
import talkapp.org.talkappmobile.module.TestDataModule;
import talkapp.org.talkappmobile.module.TestDatabaseModule;

public class ClassForInjection {
    @Inject
    PracticeWordSetInteractor practiceWordSetInteractor;
    @Inject
    LoginInteractor loginInteractor;
    @Inject
    ViewStrategyFactory viewStrategyFactory;
    @Inject
    BackendServer server;
    @Inject
    PracticeWordSetExerciseRepository exerciseRepository;
    @Inject
    Context context;

    public ClassForInjection() {
        TestDIContext context = DaggerTestDIContext.builder()
                .databaseModule(new TestDatabaseModule())
                .androidModule(new AndroidModule(new TalkappMobileApplication()))
                .audioModule(new AudioModule())
                .gameplayModule(new GameplayModule())
                .concurrentModule(new ConcurrentModule())
                .dataModule(new TestDataModule())
                .infraModule(new InfraModule())
                .languageModule(new LanguageModule())
                .backEndServiceModule(new BackEndServiceModule())
                .itemsListModule(new ItemsListModule())
                .build();

        context.inject(this);
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

    public Context getContext() {
        return context;
    }

    public ViewStrategyFactory getViewStrategyFactory() {
        return viewStrategyFactory;
    }

    public PracticeWordSetExerciseRepository getExerciseRepository() {
        return exerciseRepository;
    }
}
