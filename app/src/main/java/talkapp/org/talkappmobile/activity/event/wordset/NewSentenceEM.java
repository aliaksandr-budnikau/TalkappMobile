package talkapp.org.talkappmobile.activity.event.wordset;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public class NewSentenceEM {
    private final Sentence sentence;
    private final Word2Tokens word;
    private final boolean hideEntirely; // TODO should be removed out of here

    public NewSentenceEM(Sentence sentence, Word2Tokens word, boolean hideEntirely) {
        this.sentence = sentence;
        this.word = word;
        this.hideEntirely = hideEntirely;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public Word2Tokens getWord() {
        return word;
    }

    public boolean isHideEntirely() {
        return hideEntirely;
    }
}