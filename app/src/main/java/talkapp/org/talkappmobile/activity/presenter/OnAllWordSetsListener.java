package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public interface OnAllWordSetsListener {
    void onWordSetsInitialized(List<WordSet> wordSets);

    void onWordSetFinished(WordSet wordSet);

    void onResetExperienceClick(WordSetExperience experience);

    void onWordSetNotFinished(WordSet wordSet);
}