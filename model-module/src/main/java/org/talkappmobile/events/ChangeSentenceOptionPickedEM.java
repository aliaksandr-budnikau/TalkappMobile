package org.talkappmobile.events;

import org.talkappmobile.model.Word2Tokens;

public class ChangeSentenceOptionPickedEM {
    private final Word2Tokens word;

    public ChangeSentenceOptionPickedEM(Word2Tokens word) {
        this.word = word;
    }

    public Word2Tokens getWord() {
        return word;
    }
}