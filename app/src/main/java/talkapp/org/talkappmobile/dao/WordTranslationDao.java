package talkapp.org.talkappmobile.dao;

import java.util.List;

import talkapp.org.talkappmobile.mappings.WordTranslationMapping;

public interface WordTranslationDao {
    WordTranslationMapping findByWordAndByLanguage(String word, String language);

    void save(List<WordTranslationMapping> mappings);
}