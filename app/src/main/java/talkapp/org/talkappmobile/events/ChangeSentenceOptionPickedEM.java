package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import talkapp.org.talkappmobile.model.Word2Tokens;

public class ChangeSentenceOptionPickedEM {
    private final Word2Tokens word;

    public ChangeSentenceOptionPickedEM(@NonNull Word2Tokens word) {
        this.word = word;
    }

    @NonNull
    public Word2Tokens getWord() {
        return word;
    }
}