package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.model.WordSet;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordSetExperienceUtils {
    int getProgress(double experience, double maxTrainingExperience);

    int getMaxTrainingProgress(WordSet wordSet);
}