package talkapp.org.talkappmobile.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.db.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.db.mappings.PracticeWordSetExerciseMapping;

import static talkapp.org.talkappmobile.db.mappings.PracticeWordSetExerciseMapping.WORD_FN;
import static talkapp.org.talkappmobile.db.mappings.PracticeWordSetExerciseMapping.WORD_SET_ID_FN;

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
}