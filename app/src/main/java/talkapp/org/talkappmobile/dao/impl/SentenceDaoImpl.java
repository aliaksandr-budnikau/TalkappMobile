package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;

import static talkapp.org.talkappmobile.mappings.SentenceMapping.ID_FN;

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
        return mappings;
    }

    public List<SentenceMapping> findAllByIds(String[] ids) {
        LinkedList<SelectArg> escapedStrings = new LinkedList<>();
        for (String id : ids) {
            escapedStrings.add(new SelectArg(id));
        }
        try {
            Where<SentenceMapping, String> where = queryBuilder().where();
            where.in(ID_FN, escapedStrings);
            for (String id : ids) {
                String quote = "'";
                if (id.contains(quote)) {
                    where.or();
                    where.like(ID_FN, new SelectArg(id.replace(quote, "%")));
                }
            }
            return this.query(where.prepare());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<SentenceMapping> findAll() {
        try {
            return this.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public int deleteById(String id) {
        try {
            return super.deleteById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public SentenceMapping findById(String id) {
        try {
            return super.queryForId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}