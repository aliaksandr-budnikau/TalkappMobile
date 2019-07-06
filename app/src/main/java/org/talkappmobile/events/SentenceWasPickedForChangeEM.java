package org.talkappmobile.events;

import android.support.annotation.NonNull;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

import java.util.List;

public class SentenceWasPickedForChangeEM {
    @NonNull
    private List<Sentence> sentences;
    @NonNull
    private Word2Tokens word;

    public SentenceWasPickedForChangeEM(@NonNull List<Sentence> sentences, @NonNull Word2Tokens word) {
        this.sentences = sentences;
        this.word = word;
    }

    @NonNull
    public List<Sentence> getSentences() {
        return sentences;
    }

    @NonNull
    public Word2Tokens getWord() {
        return word;
    }
}