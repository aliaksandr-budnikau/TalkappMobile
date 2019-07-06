package org.talkappmobile.events;

import android.support.annotation.NonNull;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

public class NewSentenceEM {
    @NonNull
    private final Sentence sentence;
    @NonNull
    private final Word2Tokens word;

    public NewSentenceEM(@NonNull Sentence sentence, @NonNull Word2Tokens word) {
        this.sentence = sentence;
        this.word = word;
    }

    @NonNull
    public Sentence getSentence() {
        return sentence;
    }

    @NonNull
    public Word2Tokens getWord() {
        return word;
    }
}