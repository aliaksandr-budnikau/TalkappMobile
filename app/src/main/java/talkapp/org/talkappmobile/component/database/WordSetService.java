package talkapp.org.talkappmobile.component.database;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

public interface WordSetService {

    void resetProgress(WordSet wordSet);

    int increaseExperience(WordSet wordSet, int value);

    void moveToAnotherState(int id, WordSetProgressStatus value);
}