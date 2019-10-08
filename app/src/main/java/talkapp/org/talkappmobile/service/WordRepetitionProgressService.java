package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import java.util.List;
import java.util.Set;

public interface WordRepetitionProgressService {
    List<Sentence> findByWordAndWordSetId(Word2Tokens word);

    void save(Word2Tokens word, List<Sentence> sentences);

    void shiftSentences(Word2Tokens word);

    void cleanByWordSetId(int wordSetId);

    void createSomeIfNecessary(List<Word2Tokens> words);

    void markNewCurrentWordByWordSetIdAndWord(int wordSetId, Word2Tokens newCurrentWord);

    List<Word2Tokens> getLeftOverOfWordSetByWordSetId(int wordSetId);

    List<WordSet> findFinishedWordSetsSortByUpdatedDate(long limit, int olderThenInHours);

    int getMaxWordSetSize();

    List<WordSet> findFinishedWordSetsSortByUpdatedDate(int olderThenInHours);

    Word2Tokens getCurrentWord(int wordSetId);

    void putOffCurrentWord(int wordSetId);

    void moveCurrentWordToNextState(int wordSetId);

    int markAsRepeated(int wordIndex, int wordSetId, Sentence sentence);

    int markAsForgottenAgain(int wordIndex, int wordSetId, Sentence sentence);

    List<WordSet> findWordSetOfDifficultWords();
}