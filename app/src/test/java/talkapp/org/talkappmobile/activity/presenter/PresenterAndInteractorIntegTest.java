package talkapp.org.talkappmobile.activity.presenter;

import talkapp.org.talkappmobile.component.backend.impl.LoginException;
import talkapp.org.talkappmobile.model.LoginCredentials;

public abstract class PresenterAndInteractorIntegTest {

    private ClassForInjection injection;

    {
        injection = new ClassForInjection();
    }

    protected void login() {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("sasha-ne@tut.by");
        credentials.setPassword("password0");
        try {
            getClassForInjection().getServer().loginUser(credentials);
        } catch (LoginException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ClassForInjection getClassForInjection() {
        return injection;
    }
}