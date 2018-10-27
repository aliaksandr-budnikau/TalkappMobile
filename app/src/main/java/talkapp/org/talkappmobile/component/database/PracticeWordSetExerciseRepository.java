package talkapp.org.talkappmobile.component.database;

import java.util.Set;

import talkapp.org.talkappmobile.model.Sentence;

public interface PracticeWordSetExerciseRepository {
    Sentence findByWordAndWordSetId(String word, int wordSetId);

    void save(String word, int wordSetId, Sentence sentence);

    void cleanByWordSetId(int wordSetId);

    void createSomeIfNecessary(Set<String> words, int wordSetId);

    String peekByWordSetIdAnyWord(int wordSetId);

    String getCurrentWord(int wordSetId);

    Sentence getCurrentSentence(int wordSetId);

    void putOffCurrentWord(int wordSetId);

    void moveCurrentWordToNextState(int wordSetId);
}