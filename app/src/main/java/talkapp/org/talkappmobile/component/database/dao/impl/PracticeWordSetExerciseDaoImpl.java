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

import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.CURRENT_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.SENTENCE_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.STATUS_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.UPDATED_DATE_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.WORD_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.WORD_SET_ID_FN;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;

public class PracticeWordSetExerciseDaoImpl extends BaseDaoImpl<PracticeWordSetExerciseMapping, Integer> implements PracticeWordSetExerciseDao {

    public PracticeWordSetExerciseDaoImpl(ConnectionSource connectionSource, Class<PracticeWordSetExerciseMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<PracticeWordSetExerciseMapping> findByWordAndWordSetId(String word, int wordSetId) {
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
    public void createNewOrUpdate(PracticeWordSetExerciseMapping exercise) {
        try {
            super.createOrUpdate(exercise);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void cleanByWordSetId(int wordSetId) {
        try {
            DeleteBuilder<PracticeWordSetExerciseMapping, Integer> builder = deleteBuilder();
            builder.where().eq(WORD_SET_ID_FN, wordSetId);
            builder.delete();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public int createAll(List<PracticeWordSetExerciseMapping> words) {
        try {
            return super.create(words);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<PracticeWordSetExerciseMapping> findByStatusAndByWordSetId(WordSetExperienceStatus status, int wordSetId) {
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
    public List<PracticeWordSetExerciseMapping> findByCurrentAndByWordSetId(int wordSetId) {
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
    public List<PracticeWordSetExerciseMapping> findFinishedWordSetsSortByUpdatedDate(long limit, Date olderThenInHours) {
        try {
            QueryBuilder<PracticeWordSetExerciseMapping, Integer> builder = queryBuilder();
            Where<PracticeWordSetExerciseMapping, Integer> where = builder.where();
            where.and(
                    where.eq(STATUS_FN, FINISHED),
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
    public List<PracticeWordSetExerciseMapping> findByWordAndByStatus(String word, WordSetExperienceStatus status) {
        try {
            SelectArg selectWord = new SelectArg();
            PreparedQuery<PracticeWordSetExerciseMapping> prepare = queryBuilder()
                    .where()
                    .eq(STATUS_FN, status)
                    .and()
                    .eq(WORD_FN, selectWord).prepare();
            selectWord.setValue(word);
            return this.query(prepare);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<PracticeWordSetExerciseMapping> findByWordAndBySentenceAndByStatus(String word, String sentence, WordSetExperienceStatus status) {
        try {
            SelectArg selectWord = new SelectArg();
            SelectArg selectSentence = new SelectArg();
            PreparedQuery<PracticeWordSetExerciseMapping> prepare = queryBuilder()
                    .where()
                    .eq(STATUS_FN, status)
                    .and()
                    .eq(WORD_FN, selectWord)
                    .and()
                    .eq(SENTENCE_FN, selectSentence).prepare();
            selectWord.setValue(word);
            selectSentence.setValue(sentence);
            return this.query(prepare);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}