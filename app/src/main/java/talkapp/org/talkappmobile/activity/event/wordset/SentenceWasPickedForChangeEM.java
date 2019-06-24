package talkapp.org.talkappmobile.activity.event.wordset;

import java.util.List;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

public class SentenceWasPickedForChangeEM {
    private final List<Sentence> sentences;
    private final Word2Tokens word;

    public SentenceWasPickedForChangeEM(List<Sentence> sentences, Word2Tokens word) {
        this.sentences = sentences;
        this.word = word;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public Word2Tokens getWord() {
        return word;
    }
}