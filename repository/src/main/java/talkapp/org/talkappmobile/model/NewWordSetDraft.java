package talkapp.org.talkappmobile.model;

import android.support.annotation.NonNull;

import java.util.List;

public class NewWordSetDraft {
    private final List<WordTranslation> wordTranslations;

    public NewWordSetDraft(List<WordTranslation> wordTranslations) {
        this.wordTranslations = wordTranslations;
    }

    @NonNull
    public List<WordTranslation> getWordTranslations() {
        return wordTranslations;
    }
}