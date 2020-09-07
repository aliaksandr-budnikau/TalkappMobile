package talkapp.org.talkappmobile.listener;

import java.util.List;

import talkapp.org.talkappmobile.model.NewWordSetDraftQRObject;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

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

    void onWordSetDraftPrepare(NewWordSetDraftQRObject qrObject);

    void onWordSetCantBeShared();

    void onWordSetIsNotAvailableYet(int availableInHours);
}