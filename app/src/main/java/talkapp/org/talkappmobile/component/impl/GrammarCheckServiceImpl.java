package talkapp.org.talkappmobile.component.impl;

import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.model.GrammarError;

public class GrammarCheckServiceImpl implements GrammarCheckService {
    public static final String TAG = GrammarCheckServiceImpl.class.getSimpleName();
    private final Logger logger;

    public GrammarCheckServiceImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public List<GrammarError> check(String text) {
        logger.d(TAG, "Checking : {}", text);
        List<GrammarError> result = new ArrayList<>();
        logger.d(TAG, "Checking result: {}", result);
        return result;
    }
}
