package talkapp.org.talkappmobile.activity.presenter;

import talkapp.org.talkappmobile.component.backend.BackendServer;

public class LoginInteractor {
    private final BackendServer server;

    public LoginInteractor(BackendServer server) {
        this.server = server;
    }
}