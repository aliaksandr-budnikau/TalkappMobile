package org.talkappmobile.service.impl;

import org.androidannotations.annotations.EBean;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.service.WordSetExperienceUtils;

/**
 * @author Budnikau Aliaksandr
 */
@EBean(scope = EBean.Scope.Singleton)
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