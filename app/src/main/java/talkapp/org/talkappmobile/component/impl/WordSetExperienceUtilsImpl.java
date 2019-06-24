package talkapp.org.talkappmobile.component.impl;

import org.androidannotations.annotations.EBean;

import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import org.talkappmobile.model.WordSet;

import static org.androidannotations.annotations.EBean.Scope.Singleton;

/**
 * @author Budnikau Aliaksandr
 */
@EBean(scope = Singleton)
public class WordSetExperienceUtilsImpl implements WordSetExperienceUtils {
    @Override
    public int getProgress(double experience, double maxTrainingExperience) {
        return (int) (experience / maxTrainingExperience * 100);
    }

    @Override
    public int getMaxTrainingProgress(WordSet wordSet) {
        return wordSet.getWords().size() * 2;
    }
}