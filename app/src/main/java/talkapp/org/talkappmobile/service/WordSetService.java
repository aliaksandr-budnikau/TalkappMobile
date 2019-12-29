package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.CurrentPracticeState;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface WordSetService {

    void resetProgress(WordSet wordSet);

    int increaseExperience(int wordSetId, int value);

    void moveToAnotherState(int id, WordSetProgressStatus value);

    void remove(WordSet wordSet);

    int getCustomWordSetsStartsSince();

    WordSet createNewCustomWordSet(List<WordTranslation> translations);

    void updateWord2Tokens(Word2Tokens newWord2Tokens, Word2Tokens position);

    NewWordSetDraft getNewWordSetDraft();

    void save(NewWordSetDraft draft);

    WordSet findById(int id);

    CurrentPracticeState getCurrentPracticeState();

    void saveCurrentPracticeState(CurrentPracticeState currentPracticeState);

    void save(WordSet wordSet);
}