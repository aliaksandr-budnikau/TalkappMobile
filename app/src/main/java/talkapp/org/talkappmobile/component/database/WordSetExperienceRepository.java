package talkapp.org.talkappmobile.component.database;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public interface WordSetExperienceRepository {

    WordSetExperience findById(String id);

    WordSetExperience createNew(WordSet wordSet);

    WordSetExperience increaseExperience(String id, int value);
}