package talkapp.org.talkappmobile.component.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import talkapp.org.talkappmobile.component.database.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;

import static talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping.CURRENT_FN;
import static talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping.STATUS_FN;
import static talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping.UPDATED_DATE_FN;
import static talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping.WORD_FN;
import static talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping.WORD_SET_ID_FN;

public class WordRepetitionProgressDaoImpl extends BaseDaoImpl<WordRepetitionProgressMapping, Integer> implements WordRepetitionProgressDao {

    public WordRepetitionProgressDaoImpl(ConnectionSource connectionSource, Class<WordRepetitionProgressMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<WordRepetitionProgressMapping> findByWordAndWordSetId(String word, int wordSetId) {
        try {
            return this.query(
                    queryBuilder()
                            .where()
                            .eq(WORD_FN, word)
                            .and()
                            .eq(WORD_SET_ID_FN, wordSetId).prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void createNewOrUpdate(WordRepetitionProgressMapping exercise) {
        try {
            super.createOrUpdate(exercise);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void cleanByWordSetId(int wordSetId) {
        try {
            DeleteBuilder<WordRepetitionProgressMapping, Integer> builder = deleteBuilder();
            builder.where().eq(WORD_SET_ID_FN, wordSetId);
            builder.delete();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public int createAll(List<WordRepetitionProgressMapping> words) {
        try {
            return super.create(words);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<WordRepetitionProgressMapping> findByStatusAndByWordSetId(String status, int wordSetId) {
        try {
            return this.query(
                    queryBuilder()
                            .where()
                            .eq(STATUS_FN, status)
                            .and()
                            .eq(WORD_SET_ID_FN, wordSetId).prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<WordRepetitionProgressMapping> findByCurrentAndByWordSetId(int wordSetId) {
        try {
            return this.query(
                    queryBuilder()
                            .where()
                            .eq(CURRENT_FN, true)
                            .and()
                            .eq(WORD_SET_ID_FN, wordSetId).prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<WordRepetitionProgressMapping> findWordSetsSortByUpdatedDateAndByStatus(long limit, Date olderThenInHours, String status) {
        try {
            QueryBuilder<WordRepetitionProgressMapping, Integer> builder = queryBuilder();
            Where<WordRepetitionProgressMapping, Integer> where = builder.where();
            where.and(
                    where.eq(STATUS_FN, status),
                    where.or(
                            where.lt(UPDATED_DATE_FN, olderThenInHours),
                            where.eq(UPDATED_DATE_FN, new Date(0)),
                            where.isNull(UPDATED_DATE_FN)
                    )
            );
            builder
                    .orderBy(UPDATED_DATE_FN, false)
                    .limit(limit);
            return this.query(builder.prepare());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<WordRepetitionProgressMapping> findByWordAndByWordSetIdAndByStatus(String word, int sourceWordSetId, String status) {
        try {
            SelectArg selectWord = new SelectArg();
            PreparedQuery<WordRepetitionProgressMapping> prepare = queryBuilder()
                    .where()
                    .eq(STATUS_FN, status)
                    .and()
                    .eq(WORD_FN, selectWord)
                    .and()
                    .eq(WORD_SET_ID_FN, sourceWordSetId).prepare();
            selectWord.setValue(word);
            return this.query(prepare);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}