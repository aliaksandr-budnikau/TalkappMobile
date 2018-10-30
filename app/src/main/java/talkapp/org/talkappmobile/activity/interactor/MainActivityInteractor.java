package talkapp.org.talkappmobile.activity.interactor;

import talkapp.org.talkappmobile.component.backend.BackendServer;

public class MainActivityInteractor {

    private final BackendServer server;

    public MainActivityInteractor(BackendServer server) {
        this.server = server;
    }

    public void checkServerAvailability() {
        server.findAllTopics();
    }
}