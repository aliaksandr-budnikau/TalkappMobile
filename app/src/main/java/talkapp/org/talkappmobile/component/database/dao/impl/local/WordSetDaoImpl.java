package talkapp.org.talkappmobile.component.database.dao.impl.local;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;

public class WordSetDaoImpl extends BaseDaoImpl<WordSetMapping, String> implements WordSetDao {

    public WordSetDaoImpl(ConnectionSource connectionSource, Class<WordSetMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<WordSetMapping> findAll() {
        try {
            return this.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void save(List<WordSetMapping> mappings) {
        for (WordSetMapping mapping : mappings) {
            try {
                super.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}