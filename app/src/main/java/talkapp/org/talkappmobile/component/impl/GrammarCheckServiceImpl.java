package talkapp.org.talkappmobile.component.impl;

import java.io.IOException;
import java.util.List;

import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.TextGrammarCheckService;
import talkapp.org.talkappmobile.model.GrammarError;

public class GrammarCheckServiceImpl implements GrammarCheckService {
    public static final String TAG = GrammarCheckServiceImpl.class.getSimpleName();
    private final Logger logger;
    private final TextGrammarCheckService grammarCheckService;
    private final AuthSign authSign;

    public GrammarCheckServiceImpl(TextGrammarCheckService grammarCheckService, AuthSign authSign, Logger logger) {
        this.grammarCheckService = grammarCheckService;
        this.logger = logger;
        this.authSign = authSign;
    }

    @Override
    public List<GrammarError> check(String text) {
        logger.d(TAG, "Checking : {}", text);
        try {
            List<GrammarError> result = grammarCheckService.check(text, authSign).execute().body();
            logger.d(TAG, "Checking result: {}", result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
