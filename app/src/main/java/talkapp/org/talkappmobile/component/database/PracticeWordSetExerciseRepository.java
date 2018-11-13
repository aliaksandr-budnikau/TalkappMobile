package talkapp.org.talkappmobile.component.database;

import java.util.Set;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public interface PracticeWordSetExerciseRepository {
    Sentence findByWordAndWordSetId(Word2Tokens word, int wordSetId);

    void save(Word2Tokens word, int wordSetId, Sentence sentence);

    void cleanByWordSetId(int wordSetId);

    void createSomeIfNecessary(Set<Word2Tokens> words, int wordSetId);

    Word2Tokens peekByWordSetIdAnyWord(int wordSetId);

    Word2Tokens getCurrentWord(int wordSetId);

    Sentence getCurrentSentence(int wordSetId);

    void putOffCurrentWord(int wordSetId);

    void moveCurrentWordToNextState(int wordSetId);
}