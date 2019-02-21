package talkapp.org.talkappmobile.component.impl;

import org.androidannotations.annotations.EBean;

import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

/**
 * @author Budnikau Aliaksandr
 */
@EBean(scope = Singleton)
public class WordSetExperienceUtilsImpl implements WordSetExperienceUtils {

    @Override
    public int getProgress(WordSetExperience experience, int currentTrainingExperience) {
        return getProgress((double) currentTrainingExperience, (double) experience.getMaxTrainingExperience());
    }

    @Override
    public int getProgress(WordSetExperience experience, WordSet wordSet) {
        return getProgress((double) wordSet.getTrainingExperience(), (double) experience.getMaxTrainingExperience());
    }

    @Override
    public int getProgress(double experience, double maxTrainingExperience) {
        return (int) (experience / maxTrainingExperience * 100);
    }
}