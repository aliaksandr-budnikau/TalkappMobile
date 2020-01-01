package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public interface CurrentPracticeStateService {
    CurrentPracticeState get();

    void save(CurrentPracticeState currentPracticeState);

    List<Word2Tokens> getFinishedWords();

    List<Word2Tokens> getAllWords();

    Word2Tokens getCurrentWord();

    Sentence getCurrentSentence();

    void setCurrentSentence(Sentence sentence);
}
