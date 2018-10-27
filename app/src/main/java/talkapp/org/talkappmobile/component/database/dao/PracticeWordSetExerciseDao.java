package talkapp.org.talkappmobile.component.database.dao;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

public interface PracticeWordSetExerciseDao {

    List<PracticeWordSetExerciseMapping> findByWordAndWordSetId(String word, int wordSetId);

    Dao.CreateOrUpdateStatus createNewOrUpdate(PracticeWordSetExerciseMapping exercise);

    void cleanByWordSetId(int wordSetId);

    int createAll(List<PracticeWordSetExerciseMapping> words);

    List<PracticeWordSetExerciseMapping> findByStatusAndByWordSetId(WordSetExperienceStatus status, int wordSetId);

    List<PracticeWordSetExerciseMapping> findByCurrentAndByWordSetId(int wordSetId);
}