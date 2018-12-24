package talkapp.org.talkappmobile.activity.event.wordset;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;

public class ScoreSentenceOptionPickedEM {
    private SentenceContentScore score;
    private Sentence sentence;

    public ScoreSentenceOptionPickedEM(SentenceContentScore score, Sentence sentence) {
        this.score = score;
        this.sentence = sentence;
    }

    public SentenceContentScore getScore() {
        return score;
    }

    public Sentence getSentence() {
        return sentence;
    }
}