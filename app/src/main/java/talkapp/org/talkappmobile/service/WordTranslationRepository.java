package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;

public interface WordTranslationRepository {
    WordTranslation findByWordAndByLanguage(String word, String russian);

    void createNewOrUpdate(List<WordTranslation> wordTranslations);
}
