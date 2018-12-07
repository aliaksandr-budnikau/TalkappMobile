package talkapp.org.talkappmobile.activity.listener;

import java.util.List;

import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public interface OnWordSetsListListener {
    void onWordSetsInitialized(List<WordSet> wordSets);

    void onWordSetFinished(WordSet wordSet, int clickedItemNumber);

    void onResetExperienceClick(WordSet wordSet, WordSetExperience experience, int clickedItemNumber);

    void onWordSetNotFinished(Topic topic, WordSet wordSet);
}