package talkapp.org.talkappmobile.component.impl;

import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.SentenceService;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.UncheckedAnswer;

public class RefereeServiceImpl implements RefereeService {
    public static final int EQUALITY_THRESHOLD = 80;
    private final SentenceService serviceService;
    private final EqualityScorer equalityScorer;

    public RefereeServiceImpl(SentenceService serviceService, EqualityScorer equalityScorer) {
        this.serviceService = serviceService;
        this.equalityScorer = equalityScorer;
    }

    @Override
    public boolean checkAnswer(UncheckedAnswer answer) {
        return equalityScorer.score(answer.getExpectedAnswer(), answer.getActualAnswer(), answer.getCurrentWord()) >= EQUALITY_THRESHOLD;
    }

    @Override
    public boolean scoreCurrentSentence(Sentence sentence) {
        return serviceService.classifySentence(sentence);
    }
}
