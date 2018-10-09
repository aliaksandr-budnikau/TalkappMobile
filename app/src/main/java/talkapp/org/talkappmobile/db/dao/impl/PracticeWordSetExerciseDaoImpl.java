package talkapp.org.talkappmobile.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import talkapp.org.talkappmobile.db.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.db.mappings.PracticeWordSetExercise;

import static talkapp.org.talkappmobile.db.mappings.PracticeWordSetExercise.WORD_FN;

public class PracticeWordSetExerciseDaoImpl extends BaseDaoImpl<PracticeWordSetExercise, Integer> implements PracticeWordSetExerciseDao {

    public PracticeWordSetExerciseDaoImpl(ConnectionSource connectionSource, Class<PracticeWordSetExercise> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<PracticeWordSetExercise> findByWord(String word) {
        try {
            return this.queryForEq(WORD_FN, word);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public CreateOrUpdateStatus createNewOrUpdate(PracticeWordSetExercise exercise) {
        try {
            return super.createOrUpdate(exercise);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}