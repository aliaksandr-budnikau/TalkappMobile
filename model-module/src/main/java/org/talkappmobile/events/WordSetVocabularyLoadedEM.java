package org.talkappmobile.events;

import org.talkappmobile.model.WordTranslation;

public class WordSetVocabularyLoadedEM {
    private final WordTranslation[] translations;

    public WordSetVocabularyLoadedEM(WordTranslation[] translations) {
        this.translations = translations;
    }

    public WordTranslation[] getTranslations() {
        return translations;
    }
}