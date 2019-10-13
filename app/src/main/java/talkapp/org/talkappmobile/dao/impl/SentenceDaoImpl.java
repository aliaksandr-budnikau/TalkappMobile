package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;

import static talkapp.org.talkappmobile.mappings.SentenceMapping.ID_FN;

public class SentenceDaoImpl extends BaseDaoImpl<SentenceMapping, String> implements SentenceDao {

    private Map<String, List<SentenceMapping>> sentences = new HashMap<>();

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
        List<SentenceMapping> cached = sentences.get(getKey(word, wordsNumber));
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        List<SentenceMapping> mappings;
        try {
            mappings = this.query(
                    queryBuilder()
                            .where()
                            .like(SentenceMapping.ID_FN, new SelectArg("%\"" + word + "\"%" + wordsNumber + "%"))
                            .prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        sentences.put(getKey(word, wordsNumber), mappings);
        return mappings;
    }

    public List<SentenceMapping> findAllByIds(String[] ids) {
        LinkedList<SelectArg> escapedStrings = new LinkedList<>();
        for (String id : ids) {
            escapedStrings.add(new SelectArg(id));
        }
        try {
            return this.query(
                    queryBuilder()
                            .where()
                            .in(ID_FN, escapedStrings)
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