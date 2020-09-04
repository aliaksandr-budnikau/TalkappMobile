package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.exceptions.ObjectNotFoundException;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;

import static java.lang.String.format;
import static talkapp.org.talkappmobile.mappings.WordTranslationMapping.WORD_FN;

public class WordTranslationDaoImpl implements WordTranslationDao {

    private BaseDaoImpl<WordTranslationMapping, String> dao;

    @Inject
    public WordTranslationDaoImpl(DatabaseHelper databaseHelper) {
        try {
            dao = new BaseDaoImpl<WordTranslationMapping, String>(databaseHelper.getConnectionSource(), WordTranslationMapping.class) {
            };
            dao.initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WordTranslationMapping findByWordAndByLanguage(String word, String language) {
        List<WordTranslationMapping> mappings;
        try {
            mappings = dao.queryForEq(WORD_FN, new SelectArg(getKey(word, language)));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (mappings.isEmpty()) {
            throw new ObjectNotFoundException(format("WordTranslation word '%s' language '%s' was not found", word, language));
        } else {
            return mappings.get(0);
        }
    }

    @Override
    public void save(List<WordTranslationMapping> mappings) {
        for (WordTranslationMapping mapping : mappings) {
            try {
                dao.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private String getKey(String word, String language) {
        return word + "_" + language;
    }
}