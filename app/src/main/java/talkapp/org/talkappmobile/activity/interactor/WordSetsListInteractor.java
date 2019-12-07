package talkapp.org.talkappmobile.activity.interactor;

import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

public interface WordSetsListInteractor {
    void initializeWordSets(Topic topic, OnWordSetsListListener listener);

    void itemClick(Topic topic, WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener);

    void resetExperienceClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener);

    void deleteWordSetClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener);

    void itemLongClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener);

    void refreshWordSets(Topic topic, OnWordSetsListListener listener);

    void prepareWordSetDraftForQRCode(int wordSetId, OnWordSetsListListener listener);
}