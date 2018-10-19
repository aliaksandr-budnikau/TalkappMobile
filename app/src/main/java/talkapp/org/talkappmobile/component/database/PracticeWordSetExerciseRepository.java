package talkapp.org.talkappmobile.component.database;

import java.util.Set;

import talkapp.org.talkappmobile.model.Sentence;

public interface PracticeWordSetExerciseRepository {
    Sentence findByWordAndWordSetId(String word, String wordSetId);

    void save(String word, String wordSetId, Sentence sentence);

    void cleanByWordSetId(String wordSetId);

    void createSomeIfNecessary(Set<String> words, String wordSetId);

    String peekByWordSetIdAnyWord(String wordSetId);

    String getCurrentWord(String wordSetId);

    Sentence getCurrentSentence(String wordSetId);

    void putOffCurrentWord(String wordSetId);

    void moveCurrentWordToNextState(String wordSetId);
}