package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface WordTranslationService {
    void saveWordTranslations(final List<WordTranslation> wordTranslations);

    void saveWordTranslations(String phrase, String translation);

    void saveWordTranslations(NewWordSetDraft wordSetDraft);

    WordTranslation findByWordAndLanguage(String word, String language);
}