package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

public interface CurrentPracticeStateService {
    List<Word2Tokens> getFinishedWords();

    List<Word2Tokens> getAllWords();

    Word2Tokens getCurrentWord();

    void setCurrentWord(Word2Tokens word);

    Sentence getCurrentSentence();

    void setCurrentSentence(Sentence sentence);

    WordSet getWordSet();

    void persistWordSet();

    void set(WordSet wordSet);

    void addWordSource(Word2Tokens word);

    void changeWordSetStatus(WordSetProgressStatus status);

    void addFinishedWord(Word2Tokens word);
}
