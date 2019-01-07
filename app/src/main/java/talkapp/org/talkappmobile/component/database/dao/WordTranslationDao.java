package talkapp.org.talkappmobile.component.database.dao;

import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.local.WordTranslationMapping;

public interface WordTranslationDao {
    WordTranslationMapping findByWordAndByLanguage(String word, String language);

    void save(List<WordTranslationMapping> mappings);
}