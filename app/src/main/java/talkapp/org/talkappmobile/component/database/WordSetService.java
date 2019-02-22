package talkapp.org.talkappmobile.component.database;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

public interface WordSetService {

    WordSet findById(int id);

    WordSet createNew(WordSet wordSet);

    WordSet increaseExperience(WordSet wordSet, int value);

    WordSet moveToAnotherState(int id, WordSetExperienceStatus value);

    int getMaxTrainingProgress(WordSet wordSet);
}