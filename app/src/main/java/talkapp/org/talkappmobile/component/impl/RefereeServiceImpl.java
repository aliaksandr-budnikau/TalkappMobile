package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.model.AnswerCheckingResult;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;

public class RefereeServiceImpl implements RefereeService {
    private final GrammarCheckService grammarCheckService;
    private final EqualityScorer equalityScorer;

    public RefereeServiceImpl(GrammarCheckService grammarCheckService, EqualityScorer equalityScorer) {
        this.grammarCheckService = grammarCheckService;
        this.equalityScorer = equalityScorer;
    }

    @Override
    public AnswerCheckingResult checkAnswer(UncheckedAnswer answer) {
        AnswerCheckingResult result = new AnswerCheckingResult();
        result.setErrors(grammarCheckService.check(answer.getActualAnswer()));
        if (result.getErrors().isEmpty()) {
            if (equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer()) < 80) {
                result.setAccuracyTooLow(true);
                return result;
            }
        }
        return result;
    }

    @Override
    public boolean scoreCurrentSentence(Sentence sentence) {
        return grammarCheckService.score(sentence);
    }
}
