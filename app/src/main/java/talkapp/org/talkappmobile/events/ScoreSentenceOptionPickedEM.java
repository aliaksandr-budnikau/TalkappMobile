package talkapp.org.talkappmobile.events;

import androidx.annotation.NonNull;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;

public class ScoreSentenceOptionPickedEM {
    @NonNull
    private SentenceContentScore score;
    @NonNull
    private Sentence sentence;

    public ScoreSentenceOptionPickedEM(@NonNull SentenceContentScore score, @NonNull Sentence sentence) {
        this.score = score;
        this.sentence = sentence;
    }

    @NonNull
    public SentenceContentScore getScore() {
        return score;
    }

    @NonNull
    public Sentence getSentence() {
        return sentence;
    }
}