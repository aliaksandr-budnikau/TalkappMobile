package talkapp.org.talkappmobile.view;

import java.util.List;

import talkapp.org.talkappmobile.model.NewWordSetDraftQRObject;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

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

    void onWordSetsRefreshed(List<WordSet> wordSets, RepetitionClass repetitionClass);

    void onWordSetDraftPrepare(NewWordSetDraftQRObject qrObject);

    void onWordSetCantBeShared();

    void onWordSetIsNotAvailableYet(int availableInHours);
}