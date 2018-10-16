package talkapp.org.talkappmobile.component.database;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

public interface WordSetExperienceRepository {

    WordSetExperience findById(String id);

    WordSetExperience createNew(WordSet wordSet);

    WordSetExperience increaseExperience(String id, int value);

    WordSetExperience moveToAnotherState(String id, WordSetExperienceStatus value);
}