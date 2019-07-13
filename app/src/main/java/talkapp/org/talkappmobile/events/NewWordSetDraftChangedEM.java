package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import java.util.List;

public class NewWordSetDraftChangedEM {
    private final List<String> words;

    public NewWordSetDraftChangedEM(@NonNull List<String> words) {
        this.words = words;
    }

    @NonNull
    public List<String> getWords() {
        return words;
    }
}