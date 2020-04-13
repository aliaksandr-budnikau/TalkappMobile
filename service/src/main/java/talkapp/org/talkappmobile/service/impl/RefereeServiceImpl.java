package talkapp.org.talkappmobile.service.impl;

import talkapp.org.talkappmobile.model.UncheckedAnswer;
import talkapp.org.talkappmobile.service.EqualityScorer;
import talkapp.org.talkappmobile.service.RefereeService;

public class RefereeServiceImpl implements RefereeService {
    public static final int EQUALITY_THRESHOLD = 80;
    private final EqualityScorer equalityScorer;

    public RefereeServiceImpl(EqualityScorer equalityScorer) {
        this.equalityScorer = equalityScorer;
    }

    @Override
    public boolean checkAnswer(UncheckedAnswer answer) {
        return equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer(), answer.getCurrentWord()) >= EQUALITY_THRESHOLD;
    }
}
