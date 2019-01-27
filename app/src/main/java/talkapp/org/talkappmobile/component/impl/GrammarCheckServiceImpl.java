package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.model.Sentence;

public class GrammarCheckServiceImpl implements GrammarCheckService {
    private final DataServer server;

    public GrammarCheckServiceImpl(DataServer server) {
        this.server = server;
    }

    @Override
    public boolean score(Sentence sentence) {
        return server.saveSentenceScore(sentence);
    }
}
