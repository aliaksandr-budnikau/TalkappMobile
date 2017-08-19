package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.WordSetExperience;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordSetExperienceUtils {
    int getProgress(WordSetExperience experience, int currentTrainingExperience);

    int getProgress(WordSetExperience experience);
}