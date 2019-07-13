package talkapp.org.talkappmobile.model;

import android.support.annotation.NonNull;

import java.util.List;

public class NewWordSetDraft {
    private final List<String> words;

    public NewWordSetDraft(List<String> words) {
        this.words = words;
    }

    @NonNull
    public List<String> getWords() {
        return words;
    }
}