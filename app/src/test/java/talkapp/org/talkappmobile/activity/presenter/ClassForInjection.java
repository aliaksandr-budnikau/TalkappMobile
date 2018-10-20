package talkapp.org.talkappmobile.activity.presenter;

import javax.inject.Inject;

import talkapp.org.talkappmobile.app.TalkappMobileApplication;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.ViewStrategyFactory;
import talkapp.org.talkappmobile.component.backend.LoginService;
import talkapp.org.talkappmobile.module.AndroidModule;
import talkapp.org.talkappmobile.module.AudioModule;
import talkapp.org.talkappmobile.module.BackEndServiceModule;
import talkapp.org.talkappmobile.module.ConcurrentModule;
import talkapp.org.talkappmobile.module.DaggerTestDIContext;
import talkapp.org.talkappmobile.module.DataModule;
import talkapp.org.talkappmobile.module.GameplayModule;
import talkapp.org.talkappmobile.module.InfraModule;
import talkapp.org.talkappmobile.module.ItemsListModule;
import talkapp.org.talkappmobile.module.LanguageModule;
import talkapp.org.talkappmobile.module.TestDIContext;
import talkapp.org.talkappmobile.module.TestDatabaseModule;

public class ClassForInjection {
    @Inject
    PracticeWordSetInteractor interactor;
    @Inject
    ViewStrategyFactory viewStrategyFactory;
    @Inject
    LoginService loginService;
    @Inject
    AuthSign authSign;

    public ClassForInjection() {
        TestDIContext context = DaggerTestDIContext.builder()
                .databaseModule(new TestDatabaseModule())
                .androidModule(new AndroidModule(new TalkappMobileApplication()))
                .audioModule(new AudioModule())
                .gameplayModule(new GameplayModule())
                .concurrentModule(new ConcurrentModule())
                .dataModule(new DataModule())
                .infraModule(new InfraModule())
                .languageModule(new LanguageModule())
                .backEndServiceModule(new BackEndServiceModule())
                .itemsListModule(new ItemsListModule())
                .build();

        context.inject(this);
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public AuthSign getAuthSign() {
        return authSign;
    }

    public PracticeWordSetInteractor getInteractor() {
        return interactor;
    }

    public ViewStrategyFactory getViewStrategyFactory() {
        return viewStrategyFactory;
    }
}
