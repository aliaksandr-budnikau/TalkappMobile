package talkapp.org.talkappmobile.component.database;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public interface WordSetExperienceRepository {

    WordSetExperience createNew(WordSet wordSet);

    int increaseExperience(int id, int value);
}