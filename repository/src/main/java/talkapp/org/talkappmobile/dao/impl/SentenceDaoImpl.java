package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.exceptions.ObjectNotFoundException;
import talkapp.org.talkappmobile.mappings.SentenceMapping;

import static java.lang.String.format;
import static talkapp.org.talkappmobile.mappings.SentenceMapping.ID_FN;

public class SentenceDaoImpl implements SentenceDao {

    private final BaseDaoImpl<SentenceMapping, String> dao;

    @Inject
    public SentenceDaoImpl(DatabaseHelper databaseHelper) {
        try {
            dao = new BaseDaoImpl<SentenceMapping, String>(databaseHelper.getConnectionSource(), SentenceMapping.class) {
            };
            dao.initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(List<SentenceMapping> mappings) {
        for (SentenceMapping mapping : mappings) {
            try {
                dao.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public List<SentenceMapping> findAllByWord(String word, int wordsNumber) {
        List<SentenceMapping> mappings;
        try {
            mappings = dao.query(
                    dao.queryBuilder()
                            .where()
                            .like(SentenceMapping.TOKENS_FN, new SelectArg("%" + word + "%"))
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
            Where<SentenceMapping, String> where = dao.queryBuilder().where();
            where.in(ID_FN, escapedStrings);
            for (String id : ids) {
                String quote = "'";
                if (id.contains(quote)) {
                    where.or();
                    where.like(ID_FN, new SelectArg(id.replace(quote, "%")));
                }
            }
            return dao.query(where.prepare());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<SentenceMapping> findAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public SentenceMapping findById(String id) {
        try {
            SentenceMapping mapping = dao.queryForId(id);
            if (mapping == null) {
                throw new ObjectNotFoundException(format("Sentence id '%s' was not found", id));
            }
            return mapping;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}