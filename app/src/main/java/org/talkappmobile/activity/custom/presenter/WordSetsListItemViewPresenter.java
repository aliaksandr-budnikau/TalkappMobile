package org.talkappmobile.activity.custom.presenter;

import org.talkappmobile.activity.custom.interactor.WordSetsListItemViewInteractor;
import org.talkappmobile.activity.custom.listener.OnWordSetsListItemViewListener;
import org.talkappmobile.activity.custom.view.WordSetsListItemViewView;
import org.talkappmobile.model.WordSet;

public class WordSetsListItemViewPresenter implements OnWordSetsListItemViewListener {
    private final WordSetsListItemViewView view;
    private final WordSetsListItemViewInteractor interactor;
    private WordSet wordSet;

    public WordSetsListItemViewPresenter(WordSetsListItemViewInteractor interactor, WordSetsListItemViewView view) {
        this.interactor = interactor;
        this.view = view;
    }

    public void setModel(WordSet wordSet) {
        this.wordSet = wordSet;
    }

    public void refreshModel() {
        interactor.prepareModel(wordSet, this);
    }

    public void hideProgress() {
        view.hideProgressBar();
    }

    public void showProgress() {
        view.showProgressBar();
    }

    @Override
    public void onModelPrepared(String wordSetRowValue, int progressValue) {
        view.setWordSetRowValue(wordSetRowValue);
        view.setProgressBarValue(progressValue);
    }
}