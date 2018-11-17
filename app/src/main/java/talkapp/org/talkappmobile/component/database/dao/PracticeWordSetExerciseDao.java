package talkapp.org.talkappmobile.component.database.dao;

import java.util.List;

import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

public interface PracticeWordSetExerciseDao {

    List<PracticeWordSetExerciseMapping> findByWordAndWordSetId(String word, int wordSetId);

    void createNewOrUpdate(PracticeWordSetExerciseMapping exercise);

    void cleanByWordSetId(int wordSetId);

    int createAll(List<PracticeWordSetExerciseMapping> words);

    List<PracticeWordSetExerciseMapping> findByStatusAndByWordSetId(WordSetExperienceStatus status, int wordSetId);

    List<PracticeWordSetExerciseMapping> findByCurrentAndByWordSetId(int wordSetId);
}