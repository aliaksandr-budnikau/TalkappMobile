package talkapp.org.talkappmobile.component.database;

import talkapp.org.talkappmobile.model.Sentence;

public interface PracticeWordSetExerciseRepository {
    Sentence findByWordAndWordSetId(String word, String wordSetId);

    void save(String word, String wordSetId, Sentence sentence);

    void cleanByWordSetId(String wordSetId);
}