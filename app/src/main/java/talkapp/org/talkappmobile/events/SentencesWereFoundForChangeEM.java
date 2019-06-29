package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

import java.util.List;

public class SentencesWereFoundForChangeEM {
    @NonNull
    private List<Sentence> sentences;
    @NonNull
    private List<Sentence> alreadyPickedSentences;
    @NonNull
    private Word2Tokens word;

    public SentencesWereFoundForChangeEM(@NonNull List<Sentence> sentences, @NonNull List<Sentence> alreadyPickedSentences, @NonNull Word2Tokens word) {
        this.sentences = sentences;
        this.alreadyPickedSentences = alreadyPickedSentences;
        this.word = word;
    }

    @NonNull
    public List<Sentence> getSentences() {
        return sentences;
    }

    @NonNull
    public List<Sentence> getAlreadyPickedSentences() {
        return alreadyPickedSentences;
    }

    @NonNull
    public Word2Tokens getWord() {
        return word;
    }
}