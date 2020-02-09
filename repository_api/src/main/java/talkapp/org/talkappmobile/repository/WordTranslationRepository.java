package talkapp.org.talkappmobile.repository;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;

public interface WordTranslationRepository {
    WordTranslation findByWordAndByLanguage(String word, String russian);

    void createNewOrUpdate(List<WordTranslation> wordTranslations);
}
