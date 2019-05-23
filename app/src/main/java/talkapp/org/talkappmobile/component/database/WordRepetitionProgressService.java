package talkapp.org.talkappmobile.component.database;

import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

public interface WordRepetitionProgressService {
    Sentence findByWordAndWordSetId(Word2Tokens word, int wordSetId);

    void save(Word2Tokens word, int wordSetId, Sentence sentence);

    void cleanByWordSetId(int wordSetId);

    void createSomeIfNecessary(Set<Word2Tokens> words, int wordSetId);

    Word2Tokens peekByWordSetIdAnyWord(int wordSetId);

    Sentence getCurrentSentence(int wordSetId);

    List<WordSet> findFinishedWordSetsSortByUpdatedDate(long limit, int olderThenInHours);

    int getMaxWordSetSize();

    List<WordSet> findFinishedWordSetsSortByUpdatedDate(int olderThenInHours);

    Word2Tokens getCurrentWord(int wordSetId);

    void putOffCurrentWord(int wordSetId);

    void moveCurrentWordToNextState(int wordSetId);

    List<Sentence> findByWordAndByStatus(Word2Tokens word, WordSetProgressStatus status);

    int markAsRepeated(Word2Tokens word, Sentence sentence);

    void removeDuplicates(Word2Tokens word, Sentence sentence);
}