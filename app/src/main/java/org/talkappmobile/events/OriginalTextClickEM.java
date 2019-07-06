package org.talkappmobile.events;

import android.support.annotation.NonNull;

import org.talkappmobile.model.Word2Tokens;

public class OriginalTextClickEM {
    @NonNull
    private Word2Tokens word;

    public OriginalTextClickEM(@NonNull Word2Tokens word) {
        this.word = word;
    }

    @NonNull
    public Word2Tokens getWord() {
        return word;
    }
}