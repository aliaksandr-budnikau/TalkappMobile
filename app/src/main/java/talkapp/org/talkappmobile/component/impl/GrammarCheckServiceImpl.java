package talkapp.org.talkappmobile.component.impl;

import java.util.List;

import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.Sentence;

public class GrammarCheckServiceImpl implements GrammarCheckService {
    private static final String TAG = GrammarCheckServiceImpl.class.getSimpleName();
    private final Logger logger;
    private final DataServer server;

    public GrammarCheckServiceImpl(DataServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Override
    public List<GrammarError> check(String text) {
        logger.d(TAG, "Checking : {}", text);
        List<GrammarError> result = server.checkText(text);
        logger.d(TAG, "Checking result: {}", result);
        return result;
    }

    @Override
    public boolean score(Sentence sentence) {
        return server.saveSentenceScore(sentence);
    }
}
