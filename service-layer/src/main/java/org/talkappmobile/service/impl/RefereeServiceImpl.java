package org.talkappmobile.service.impl;

import org.talkappmobile.model.UncheckedAnswer;
import org.talkappmobile.service.EqualityScorer;
import org.talkappmobile.service.RefereeService;

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
