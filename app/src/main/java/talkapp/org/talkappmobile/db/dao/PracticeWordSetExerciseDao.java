package talkapp.org.talkappmobile.db.dao;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import talkapp.org.talkappmobile.db.mappings.PracticeWordSetExercise;

public interface PracticeWordSetExerciseDao {

    List<PracticeWordSetExercise> findByWord(String word);

    Dao.CreateOrUpdateStatus createNewOrUpdate(PracticeWordSetExercise exercise);
}