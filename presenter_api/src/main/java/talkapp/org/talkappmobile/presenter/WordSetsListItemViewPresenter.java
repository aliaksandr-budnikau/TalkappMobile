package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.model.WordSet;

public interface WordSetsListItemViewPresenter {
    void setModel(WordSet wordSet);

    void refreshModel();

    void hideProgress();

    void showProgress();
}
