package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.WordTranslation;

public interface WordTranslationService {
    void saveWordTranslations(final List<WordTranslation> wordTranslations);
}