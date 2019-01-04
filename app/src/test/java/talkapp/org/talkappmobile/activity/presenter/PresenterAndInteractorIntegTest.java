package talkapp.org.talkappmobile.activity.presenter;

import org.powermock.reflect.Whitebox;

import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.impl.AuthorizationInterceptor;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.backend.impl.LoginException;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.model.LoginCredentials;

public abstract class PresenterAndInteractorIntegTest {

    private BackendServer server;

    private AuthSign authSign;

    {
        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        authSign = new AuthSign();
        Whitebox.setInternalState(factory, "authSign", authSign);
        Whitebox.setInternalState(factory, "authorizationInterceptor", new AuthorizationInterceptor());
        server = factory.get();
    }

    protected void login() {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("sasha-ne@tut.by");
        credentials.setPassword("password0");
        String signature;
        try {
            signature = server.loginUser(credentials);
        } catch (LoginException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        authSign.put(signature);
    }

    public BackendServer getServer() {
        return server;
    }
}