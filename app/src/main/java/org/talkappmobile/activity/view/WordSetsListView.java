package org.talkappmobile.activity.view;

import org.talkappmobile.model.RepetitionClass;
import org.talkappmobile.model.Topic;
import org.talkappmobile.model.WordSet;

import java.util.List;

public interface WordSetsListView {
    void onWordSetsInitialized(List<WordSet> wordSets, RepetitionClass repetitionClass);

    void onWordSetFinished(WordSet wordSet, int clickedItemNumber);

    void onResetExperienceClick(WordSet wordSet, int clickedItemNumber);

    void onWordSetNotFinished(Topic topic, WordSet wordSet);

    void onItemLongClick(WordSet wordSet, int clickedItemNumber);

    void onInitializeBeginning();

    void onInitializeEnd();

    void onWordSetRemoved(WordSet wordSet, int clickedItemNumber);

    void onWordSetNotRemoved();

    void onWordSetTooSmallForRepetition(int maxWordSetSize, int actualSize);
}