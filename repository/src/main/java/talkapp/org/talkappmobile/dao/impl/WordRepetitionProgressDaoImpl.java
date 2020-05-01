package talkapp.org.talkappmobile.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;

import static java.util.Arrays.asList;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.DUMMY_VALUE_DEPRECATED;

public class WordRepetitionProgressDaoImpl implements WordRepetitionProgressDao {

    private final BaseDaoImpl<WordRepetitionProgressMapping, Integer> dao;

    @Inject
    public WordRepetitionProgressDaoImpl(DatabaseHelper databaseHelper) {
        try {
            dao = new BaseDaoImpl<WordRepetitionProgressMapping, Integer>(databaseHelper.getConnectionSource(), WordRepetitionProgressMapping.class) {
            };
            dao.initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WordRepetitionProgressMapping> findByWordIndexAndWordSetId(int wordIndex, int wordSetId) {
        try {
            return dao.query(
                    dao.queryBuilder()
                            .where()
                            .eq(WordRepetitionProgressMapping.WORD_INDEX_FN, new SelectArg(wordIndex))
                            .and()
                            .eq(WordRepetitionProgressMapping.WORD_SET_ID_FN, wordSetId).prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void createNewOrUpdate(WordRepetitionProgressMapping exercise) {
        try {
            setDummyValueToNotNullableFields(asList(exercise));
            dao.createOrUpdate(exercise);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void cleanByWordSetId(int wordSetId) {
        try {
            DeleteBuilder<WordRepetitionProgressMapping, Integer> builder = dao.deleteBuilder();
            builder.where().eq(WordRepetitionProgressMapping.WORD_SET_ID_FN, wordSetId);
            builder.delete();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public int createAll(List<WordRepetitionProgressMapping> words) {
        try {
            setDummyValueToNotNullableFields(words);
            return dao.create(words);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void setDummyValueToNotNullableFields(List<WordRepetitionProgressMapping> words) {
        for (WordRepetitionProgressMapping word : words) {
            if (word.getWordJSON() == null) {
                word.setWordJSON(DUMMY_VALUE_DEPRECATED);
            }
        }
    }

    @Override
    public List<WordRepetitionProgressMapping> findByStatusAndByWordSetId(String status, int wordSetId) {
        try {
            return dao.query(
                    dao.queryBuilder()
                            .where()
                            .eq(WordRepetitionProgressMapping.STATUS_FN, status)
                            .and()
                            .eq(WordRepetitionProgressMapping.WORD_SET_ID_FN, wordSetId).prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<WordRepetitionProgressMapping> findByCurrentAndByWordSetId(int wordSetId) {
        try {
            return dao.query(
                    dao.queryBuilder()
                            .where()
                            .eq(WordRepetitionProgressMapping.CURRENT_FN, true)
                            .and()
                            .eq(WordRepetitionProgressMapping.WORD_SET_ID_FN, wordSetId).prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<WordRepetitionProgressMapping> findWordSetsSortByUpdatedDateAndByStatus(long limit, Date olderThenInHours, String status) {
        try {
            QueryBuilder<WordRepetitionProgressMapping, Integer> builder = dao.queryBuilder();
            Where<WordRepetitionProgressMapping, Integer> where = builder.where();
            where.and(
                    where.eq(WordRepetitionProgressMapping.STATUS_FN, status),
                    where.or(
                            where.lt(WordRepetitionProgressMapping.UPDATED_DATE_FN, olderThenInHours),
                            where.eq(WordRepetitionProgressMapping.UPDATED_DATE_FN, new Date(0)),
                            where.isNull(WordRepetitionProgressMapping.UPDATED_DATE_FN)
                    )
            );
            builder
                    .orderBy(WordRepetitionProgressMapping.UPDATED_DATE_FN, false)
                    .limit(limit);
            return dao.query(builder.prepare());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<WordRepetitionProgressMapping> findByWordIndexAndByWordSetIdAndByStatus(int wordIndex, int sourceWordSetId, String status) {
        try {
            SelectArg selectWord = new SelectArg();
            PreparedQuery<WordRepetitionProgressMapping> prepare = dao.queryBuilder()
                    .where()
                    .eq(WordRepetitionProgressMapping.STATUS_FN, status)
                    .and()
                    .eq(WordRepetitionProgressMapping.WORD_INDEX_FN, selectWord)
                    .and()
                    .eq(WordRepetitionProgressMapping.WORD_SET_ID_FN, sourceWordSetId).prepare();
            selectWord.setValue(wordIndex);
            return dao.query(prepare);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<WordRepetitionProgressMapping> findAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}