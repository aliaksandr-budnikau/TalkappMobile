package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public interface WordSetExperienceRepository {

    WordSetExperience createNew(WordSet wordSet);
}