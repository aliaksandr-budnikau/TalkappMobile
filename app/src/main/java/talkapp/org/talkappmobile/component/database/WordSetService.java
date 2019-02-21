package talkapp.org.talkappmobile.component.database;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

public interface WordSetService {

    WordSetExperience findById(int id);

    WordSetExperience createNew(WordSet wordSet);

    WordSetExperience increaseExperience(WordSet wordSet, int value);

    WordSetExperience moveToAnotherState(int id, WordSetExperienceStatus value);
}