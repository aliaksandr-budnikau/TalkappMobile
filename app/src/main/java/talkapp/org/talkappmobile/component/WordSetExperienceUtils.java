package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.model.WordSet;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordSetExperienceUtils {
    @Deprecated
    int getProgress(WordSet wordSet, int currentTrainingExperience);

    @Deprecated
    int getProgress(WordSet wordSet);

    int getProgress(double experience, double maxTrainingExperience);
}