package talkapp.org.talkappmobile.events;

import android.support.annotation.NonNull;

import org.talkappmobile.model.WordTranslation;

public class WordSetVocabularyLoadedEM {
    @NonNull
    private WordTranslation[] translations;

    public WordSetVocabularyLoadedEM(@NonNull WordTranslation[] translations) {
        this.translations = translations;
    }

    @NonNull
    public WordTranslation[] getTranslations() {
        return translations;
    }
}