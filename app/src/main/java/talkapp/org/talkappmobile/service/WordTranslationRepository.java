package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.WordTranslation;

public interface WordTranslationRepository {
    WordTranslation findByWordAndByLanguage(String word, String russian);
}
