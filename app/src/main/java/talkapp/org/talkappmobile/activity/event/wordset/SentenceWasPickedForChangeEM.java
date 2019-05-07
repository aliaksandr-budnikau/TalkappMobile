package talkapp.org.talkappmobile.activity.event.wordset;

import talkapp.org.talkappmobile.model.Sentence;

public class SentenceWasPickedForChangeEM {
    private final Sentence sentence;

    public SentenceWasPickedForChangeEM(Sentence sentence) {
        this.sentence = sentence;
    }

    public Sentence getSentence() {
        return sentence;
    }
}