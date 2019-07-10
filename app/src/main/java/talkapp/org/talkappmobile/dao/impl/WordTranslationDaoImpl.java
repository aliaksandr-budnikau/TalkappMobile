package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordTranslationDaoImpl extends BaseDaoImpl<WordTranslationMapping, String> implements WordTranslationDao {

    private Map<String, WordTranslationMapping> wordTranslations = new HashMap<>();

    public WordTranslationDaoImpl(ConnectionSource connectionSource, Class<WordTranslationMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public WordTranslationMapping findByWordAndByLanguage(String word, String language) {
        WordTranslationMapping cached = wordTranslations.get(getKey(word, language));
        if (cached != null) {
            return cached;
        }
        WordTranslationMapping mapping;
        try {
            mapping = this.queryForId(getKey(word, language));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        wordTranslations.put(getKey(word, language), mapping);
        return mapping;
    }

    @Override
    public void save(List<WordTranslationMapping> mappings) {
        for (WordTranslationMapping mapping : mappings) {
            if (wordTranslations.get(getKey(mapping.getWord(), mapping.getLanguage())) != null) {
                continue;
            }
            try {
                super.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            wordTranslations.put(getKey(mapping.getWord(), mapping.getLanguage()), mapping);
        }
    }

    private String getKey(String word, String language) {
        return word + "_" + language;
    }
}