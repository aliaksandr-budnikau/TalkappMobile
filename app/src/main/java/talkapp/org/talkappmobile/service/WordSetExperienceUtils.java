package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.WordSetExperience;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordSetExperienceUtils {
    @Deprecated
    int getProgress(WordSetExperience experience, int currentTrainingExperience);

    @Deprecated
    int getProgress(WordSetExperience experience);

    int getProgress(double experience, double maxTrainingExperience);
}