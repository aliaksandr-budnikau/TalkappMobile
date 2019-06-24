package org.talkappmobile.events;

import org.talkappmobile.model.Word2Tokens;

public class OriginalTextClickEM {
    private final Word2Tokens word;

    public OriginalTextClickEM(Word2Tokens word) {
        this.word = word;
    }

    public Word2Tokens getWord() {
        return word;
    }
}