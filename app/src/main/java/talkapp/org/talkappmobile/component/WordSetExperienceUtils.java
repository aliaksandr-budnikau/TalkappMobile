package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordSetExperienceUtils {
    @Deprecated
    int getProgress(WordSetExperience experience, int currentTrainingExperience);

    @Deprecated
    int getProgress(WordSetExperience experience, WordSet wordSet);

    int getProgress(double experience, double maxTrainingExperience);
}