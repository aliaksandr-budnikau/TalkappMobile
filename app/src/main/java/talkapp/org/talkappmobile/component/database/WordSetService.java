package talkapp.org.talkappmobile.component.database;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

public interface WordSetService {

    void resetProgress(WordSet wordSet);

    int increaseExperience(WordSet wordSet, int value);

    WordSet moveToAnotherState(int id, WordSetExperienceStatus value);
}