package talkapp.org.talkappmobile.component.database.dao.impl.local;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping;

import static talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping.ID_FN;

public class SentenceDaoImpl extends BaseDaoImpl<SentenceMapping, String> implements SentenceDao {

    public SentenceDaoImpl(ConnectionSource connectionSource, Class<SentenceMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public void save(List<SentenceMapping> mappings) {
        for (SentenceMapping mapping : mappings) {
            try {
                super.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public List<SentenceMapping> findAllByWord(String word, int wordsNumber) {
        try {
            return this.query(
                    queryBuilder()
                            .where()
                            .like(ID_FN, "%#" + word + "#" + wordsNumber)
                            .prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}