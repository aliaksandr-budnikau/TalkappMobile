package talkapp.org.talkappmobile.service.impl;

import talkapp.org.talkappmobile.model.WordSetExperience;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;

/**
 * @author Budnikau Aliaksandr
 */
public class WordSetExperienceUtilsImpl implements WordSetExperienceUtils {

    @Override
    public int getProgress(WordSetExperience experience, int currentTrainingExperience) {
        return getProgress((double) currentTrainingExperience, (double) experience.getMaxTrainingExperience());
    }

    @Override
    public int getProgress(WordSetExperience experience) {
        return getProgress((double) experience.getTrainingExperience(), (double) experience.getMaxTrainingExperience());
    }

    private int getProgress(double experience, double maxTrainingExperience) {
        return (int) (experience / maxTrainingExperience * 100);
    }
}