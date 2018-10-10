package talkapp.org.talkappmobile.component.impl;

import org.languagetool.JLanguageTool;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.model.GrammarError;

public class GrammarCheckServiceImpl implements GrammarCheckService {
    public static final String TAG = GrammarCheckServiceImpl.class.getSimpleName();
    private final JLanguageTool languageTool;
    private final Logger logger;

    public GrammarCheckServiceImpl(JLanguageTool languageTool, Logger logger) {
        this.languageTool = languageTool;
        this.logger = logger;
    }

    @Override
    public List<GrammarError> check(String text) {
        logger.d(TAG, "Checking : {}", text);
        try {
            List<RuleMatch> ruleMatches = languageTool.check(text);
            List<GrammarError> result = new ArrayList<>();
            for (RuleMatch ruleMatch : ruleMatches) {
                GrammarError error = new GrammarError();
                result.add(error);
                error.setMessage(ruleMatch.getShortMessage());
                String bad = text.substring(ruleMatch.getFromPos(), ruleMatch.getToPos());
                error.setBad(bad);
                error.setOffset(ruleMatch.getFromPos());
                error.setLength(ruleMatch.getToPos() - ruleMatch.getFromPos());
                error.setSuggestions(ruleMatch.getSuggestedReplacements());
            }
            logger.d(TAG, "Checking result: {}", result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
