package talkapp.org.talkappmobile.component.database;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

public interface WordSetService {

    WordSet findById(int id);

    void resetProgress(WordSet wordSet);

    WordSet increaseExperience(WordSet wordSet, int value);

    WordSet moveToAnotherState(int id, WordSetExperienceStatus value);
}