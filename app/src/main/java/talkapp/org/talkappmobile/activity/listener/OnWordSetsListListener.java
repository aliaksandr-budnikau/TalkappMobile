package talkapp.org.talkappmobile.activity.listener;

import java.util.List;

import org.talkappmobile.model.Topic;
import org.talkappmobile.model.WordSet;

public interface OnWordSetsListListener {
    void onWordSetsInitialized(List<WordSet> wordSets);

    void onWordSetFinished(WordSet wordSet, int clickedItemNumber);

    void onResetExperienceClick(WordSet wordSet, int clickedItemNumber);

    void onWordSetNotFinished(Topic topic, WordSet wordSet);

    void onWordSetRemoved(WordSet wordSet, int clickedItemNumber);

    void onWordSetNotRemoved(WordSet wordSet, int clickedItemNumber);

    void itemLongClick(WordSet wordSet, int clickedItemNumber);

    void onItemLongClick(WordSet wordSet, int clickedItemNumber);

    void onWordSetTooSmallForRepetition(int maxWordSetSize, int actualSize);
}