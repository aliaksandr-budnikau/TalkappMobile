package org.talkappmobile.dao;

import java.util.List;

import org.talkappmobile.mappings.WordTranslationMapping;

public interface WordTranslationDao {
    WordTranslationMapping findByWordAndByLanguage(String word, String language);

    void save(List<WordTranslationMapping> mappings);
}