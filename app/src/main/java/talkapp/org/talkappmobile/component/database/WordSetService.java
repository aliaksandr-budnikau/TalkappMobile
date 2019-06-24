package talkapp.org.talkappmobile.component.database;

import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordSetProgressStatus;

public interface WordSetService {

    void resetProgress(WordSet wordSet);

    int increaseExperience(WordSet wordSet, int value);

    void moveToAnotherState(int id, WordSetProgressStatus value);

    void remove(WordSet wordSet);

    int getCustomWordSetsStartsSince();
}