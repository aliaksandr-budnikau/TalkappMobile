package org.talkappmobile.activity.listener;

import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.Topic;
import org.talkappmobile.model.WordSet;

import java.util.List;

public interface OnWordSetsListListener {
    void onWordSetsInitialized(List<WordSet> wordSets, RepetitionClass repetitionClass);

    void onWordSetFinished(WordSet wordSet, int clickedItemNumber);

    void onResetExperienceClick(WordSet wordSet, int clickedItemNumber);

    void onWordSetNotFinished(Topic topic, WordSet wordSet);

    void onWordSetRemoved(WordSet wordSet, int clickedItemNumber);

    void onWordSetNotRemoved(WordSet wordSet, int clickedItemNumber);

    void itemLongClick(WordSet wordSet, int clickedItemNumber);

    void onItemLongClick(WordSet wordSet, int clickedItemNumber);

    void onWordSetTooSmallForRepetition(int maxWordSetSize, int actualSize);

    void onWordSetsFetched(List<WordSet> wordSets, RepetitionClass repetitionClass);
}