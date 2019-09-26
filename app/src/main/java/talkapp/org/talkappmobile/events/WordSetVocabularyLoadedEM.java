package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;

public class WordSetVocabularyLoadedEM {
    @NonNull
    private List<WordTranslation> translations;

    public WordSetVocabularyLoadedEM(@NonNull List<WordTranslation> translations) {
        this.translations = translations;
    }

    @NonNull
    public List<WordTranslation> getTranslations() {
        return translations;
    }
}