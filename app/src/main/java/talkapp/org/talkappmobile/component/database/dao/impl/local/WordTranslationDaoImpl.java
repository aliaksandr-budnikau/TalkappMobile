package talkapp.org.talkappmobile.component.database.dao.impl.local;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.component.database.dao.WordTranslationDao;
import talkapp.org.talkappmobile.component.database.mappings.local.WordTranslationMapping;

public class WordTranslationDaoImpl extends BaseDaoImpl<WordTranslationMapping, String> implements WordTranslationDao {

    public WordTranslationDaoImpl(ConnectionSource connectionSource, Class<WordTranslationMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public WordTranslationMapping findByWordAndByLanguage(String word, String language) {
        try {
            return this.queryForId(word + "_" + language);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void save(List<WordTranslationMapping> mappings) {
        for (WordTranslationMapping mapping : mappings) {
            try {
                super.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}