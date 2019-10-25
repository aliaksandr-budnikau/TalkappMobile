package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.NewWordSetDraft;
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

    NewWordSetDraft getNewWordSetDraft();

    void save(NewWordSetDraft draft);

    WordSet findById(int id);
}