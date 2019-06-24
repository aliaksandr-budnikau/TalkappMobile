package talkapp.org.talkappmobile.activity.event.wordset;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

public class NewSentenceEM {
    private final Sentence sentence;
    private final Word2Tokens word;

    public NewSentenceEM(Sentence sentence, Word2Tokens word) {
        this.sentence = sentence;
        this.word = word;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public Word2Tokens getWord() {
        return word;
    }
}