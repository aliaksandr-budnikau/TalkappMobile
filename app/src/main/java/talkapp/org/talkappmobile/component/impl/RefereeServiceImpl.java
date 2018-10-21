package talkapp.org.talkappmobile.component.impl;

import java.util.Iterator;
import java.util.List;

import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.UncheckedAnswer;

public class RefereeServiceImpl implements RefereeService {
    private final GrammarCheckService grammarCheckService;
    private final EqualityScorer equalityScorer;
    private final String[] ignoredErrors = {
            "Punctuation error, sentence looks like a fragment in"
    };

    public RefereeServiceImpl(GrammarCheckService grammarCheckService, EqualityScorer equalityScorer) {
        this.grammarCheckService = grammarCheckService;
        this.equalityScorer = equalityScorer;
    }

    @Override
    public AnswerCheckingResult checkAnswer(UncheckedAnswer answer) {
        AnswerCheckingResult result = new AnswerCheckingResult();
        result.setErrors(grammarCheckService.check(answer.getActualAnswer()));
        removeIgnoredErrors(result.getErrors());
        if (result.getErrors().isEmpty()) {
            if (equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer()) < 80) {
                result.setAccuracyTooLow(true);
                return result;
            }
        }
        return result;
    }

    public void removeIgnoredErrors(List<GrammarError> errors) {
        Iterator<GrammarError> iterator = errors.iterator();
        ERROR:
        while (iterator.hasNext()) {
            GrammarError error = iterator.next();
            for (String ignoredError : ignoredErrors) {
                if (error.getMessage().contains(ignoredError)) {
                    iterator.remove();
                    continue ERROR;
                }
            }
        }
    }
}
