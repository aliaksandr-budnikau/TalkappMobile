package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.RefereeService;
import org.talkappmobile.model.UncheckedAnswer;

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
