package talkapp.org.talkappmobile.component.database.dao.impl.local;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping;

import static talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping.ID_FN;

public class SentenceDaoImpl extends BaseDaoImpl<SentenceMapping, String> implements SentenceDao {

    private Map<String, List<SentenceMapping>> sentences = new HashMap<>();

    public SentenceDaoImpl(ConnectionSource connectionSource, Class<SentenceMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public void save(List<SentenceMapping> mappings) {
        for (SentenceMapping mapping : mappings) {
            String[] ids = mapping.getId().split("#");
            String key = getKey(ids[1], Integer.valueOf(ids[2]));
            List<SentenceMapping> list = sentences.get(key);
            if (list == null) {
                sentences.put(key, new LinkedList<SentenceMapping>());
            }
            try {
                super.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            List<SentenceMapping> cached = sentences.get(key);
            cached.remove(mapping);
            cached.add(mapping);
        }
    }

    @Override
    public List<SentenceMapping> findAllByWord(String word, int wordsNumber) {
        List<SentenceMapping> cached = sentences.get(getKey(word, wordsNumber));
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        List<SentenceMapping> mappings;
        try {
            mappings = this.query(
                    queryBuilder()
                            .where()
                            .like(ID_FN, "%#" + word + "#" + wordsNumber)
                            .prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        sentences.put(getKey(word, wordsNumber), mappings);
        return mappings;
    }

    public List<SentenceMapping> findAllByIds(String[] ids) {
        try {
            return this.query(
                    queryBuilder()
                            .where()
                            .in(ID_FN, ids)
                            .prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getKey(String word, int wordsNumber) {
        return word + "_" + wordsNumber;
    }
}