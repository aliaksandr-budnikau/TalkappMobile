package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import java.util.List;

public class NewWordSetDraftWasChangedEM {
    private final List<String> words;

    public NewWordSetDraftWasChangedEM(@NonNull List<String> words) {
        this.words = words;
    }

    @NonNull
    public List<String> getWords() {
        return words;
    }
}