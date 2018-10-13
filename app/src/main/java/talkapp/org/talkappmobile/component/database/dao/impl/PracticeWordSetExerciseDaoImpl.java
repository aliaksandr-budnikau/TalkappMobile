package talkapp.org.talkappmobile.component.database.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;

import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.WORD_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.WORD_SET_ID_FN;

public class PracticeWordSetExerciseDaoImpl extends BaseDaoImpl<PracticeWordSetExerciseMapping, Integer> implements PracticeWordSetExerciseDao {

    public PracticeWordSetExerciseDaoImpl(ConnectionSource connectionSource, Class<PracticeWordSetExerciseMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<PracticeWordSetExerciseMapping> findByWordAndWordSetId(String word, String wordSetId) {
        try {
            return this.query(
                    queryBuilder()
                            .where()
                            .eq(WORD_FN, word)
                            .eq(WORD_SET_ID_FN, wordSetId).prepare()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public CreateOrUpdateStatus createNewOrUpdate(PracticeWordSetExerciseMapping exercise) {
        try {
            return super.createOrUpdate(exercise);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void cleanByWordSetId(String wordSetId) {
        try {
            DeleteBuilder<PracticeWordSetExerciseMapping, Integer> builder = deleteBuilder();
            builder.where().eq(WORD_SET_ID_FN, wordSetId);
            builder.delete();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}