package talkapp.org.talkappmobile.component.database;

import java.util.List;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public interface WordSetExperienceRepository {

    List<WordSetExperience> findAll();

    WordSetExperience createNew(WordSet wordSet);

    int increaseExperience(String id, int value);
}