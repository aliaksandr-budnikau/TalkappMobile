package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.model.Sentence;

public interface PracticeWordSetExerciseTempRepository {
    Sentence findByWordAndWordSetId(String word, String wordSetId);

    void save(String word, String wordSetId, Sentence sentence);
}