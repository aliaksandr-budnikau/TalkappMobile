package talkapp.org.talkappmobile.events;

import androidx.annotation.NonNull;

import talkapp.org.talkappmobile.model.Word2Tokens;

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