package talkapp.org.talkappmobile.db.dao;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import talkapp.org.talkappmobile.db.mappings.PracticeWordSetExerciseMapping;

public interface PracticeWordSetExerciseDao {

    List<PracticeWordSetExerciseMapping> findByWordAndWordSetId(String word, String wordSetId);

    Dao.CreateOrUpdateStatus createNewOrUpdate(PracticeWordSetExerciseMapping exercise);
}