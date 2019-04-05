package talkapp.org.talkappmobile.activity.event.wordset;

import talkapp.org.talkappmobile.model.WordTranslation;

public class WordSetVocabularyLoadedEM {
    private final WordTranslation[] translations;

    public WordSetVocabularyLoadedEM(WordTranslation[] translations) {
        this.translations = translations;
    }

    public WordTranslation[] getTranslations() {
        return translations;
    }
}