package talkapp.org.talkappmobile.events;

import androidx.annotation.NonNull;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;

public class NewWordSetDraftWasChangedEM {
    private final List<WordTranslation> wordTranslations;

    public NewWordSetDraftWasChangedEM(@NonNull List<WordTranslation> wordTranslations) {
        this.wordTranslations = wordTranslations;
    }

    @NonNull
    public List<WordTranslation> getWordTranslations() {
        return wordTranslations;
    }
}