package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.model.WordSet;

public interface WordSetsListPresenter {
    void initialize();

    void itemClick(WordSet wordSet, int clickedItemNumber);

    void resetExperienceClick(WordSet wordSet, int clickedItemNumber);

    void deleteWordSetClick(WordSet wordSet, int clickedItemNumber);

    void refresh();

    void prepareWordSetDraftForQRCode(int wordSetId);

    void itemLongClick(WordSet wordSet, int position);
}
