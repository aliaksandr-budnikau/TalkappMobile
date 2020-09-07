package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordRepetitionProgress;
import talkapp.org.talkappmobile.model.WordSet;

public interface WordRepetitionProgressService {

    void save(Word2Tokens word, List<Sentence> sentences);

    void shiftSentences(Word2Tokens word);

    void cleanByWordSetId(int wordSetId);

    void createSomeIfNecessary(List<Word2Tokens> words);

    List<Word2Tokens> getLeftOverOfWordSetByWordSetId(int wordSetId);

    List<WordSet> findFinishedWordSetsSortByUpdatedDate(long limit, int olderThenInHours);

    int getMaxWordSetSize();

    List<WordSet> findFinishedWordSetsSortByUpdatedDate(int olderThenInHours);

    void moveCurrentWordToNextState(Word2Tokens word);

    int markAsRepeated(Word2Tokens word);

    int getRepetitionCounter(Word2Tokens word);

    int markAsForgottenAgain(Word2Tokens word);

    List<WordSet> findWordSetOfDifficultWords();

    void updateSentenceIds(Word2Tokens newWord2Token, Word2Tokens oldWord2Token);

    List<WordRepetitionProgress> findByWordIndexAndByWordSetIdAndByStatus(int wordIndex, int wordSetId, String status);

    void createNewOrUpdate(WordRepetitionProgress exercise);

    List<WordRepetitionProgress> findAll();
}