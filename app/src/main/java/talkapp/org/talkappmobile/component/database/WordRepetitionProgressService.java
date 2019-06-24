package talkapp.org.talkappmobile.component.database;

import java.util.List;
import java.util.Set;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;

public interface WordRepetitionProgressService {
    List<Sentence> findByWordAndWordSetId(Word2Tokens word);

    void save(Word2Tokens word, List<Sentence> sentences);

    void shiftSentences(Word2Tokens word);

    void cleanByWordSetId(int wordSetId);

    void createSomeIfNecessary(Set<Word2Tokens> words);

    void markNewCurrentWordByWordSetIdAndWord(int wordSetId, Word2Tokens newCurrentWord);

    List<Word2Tokens> getLeftOverOfWordSetByWordSetId(int wordSetId);

    List<WordSet> findFinishedWordSetsSortByUpdatedDate(long limit, int olderThenInHours);

    int getMaxWordSetSize();

    List<WordSet> findFinishedWordSetsSortByUpdatedDate(int olderThenInHours);

    Word2Tokens getCurrentWord(int wordSetId);

    void putOffCurrentWord(int wordSetId);

    void moveCurrentWordToNextState(int wordSetId);

    int markAsRepeated(Word2Tokens word, Sentence sentence);

    int markAsForgottenAgain(Word2Tokens word, Sentence sentence);
}