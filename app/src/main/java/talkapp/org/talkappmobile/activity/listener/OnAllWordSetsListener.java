package talkapp.org.talkappmobile.activity.listener;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public interface OnAllWordSetsListener {
    void onWordSetsInitialized(List<WordSet> wordSets);

    void onWordSetFinished(WordSet wordSet);

    void onResetExperienceClick(WordSetExperience experience);

    void onWordSetNotFinished(Topic topic, WordSet wordSet);
}