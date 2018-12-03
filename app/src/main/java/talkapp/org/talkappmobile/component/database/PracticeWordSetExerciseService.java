package talkapp.org.talkappmobile.component.database;

import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public interface PracticeWordSetExerciseService {
    Sentence findByWordAndWordSetId(Word2Tokens word, int wordSetId);

    void save(Word2Tokens word, int wordSetId, Sentence sentence);

    void cleanByWordSetId(int wordSetId);

    void createSomeIfNecessary(Set<Word2Tokens> words, int wordSetId);

    Word2Tokens peekByWordSetIdAnyWord(int wordSetId);

    Sentence getCurrentSentence(int wordSetId);

    List<WordSet> findFinishedWordSetsSortByUpdatedDate(int limit, int olderThenInHours);

    List<WordSet> findFinishedWordSetsSortByUpdatedDate(int olderThenInHours);

    void putOffCurrentWord(int wordSetId);

    void moveCurrentWordToNextState(int wordSetId);
}