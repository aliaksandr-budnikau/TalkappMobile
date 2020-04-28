package talkapp.org.talkappmobile.presenter;

import talkapp.org.talkappmobile.interactor.WordSetsListItemViewInteractor;
import talkapp.org.talkappmobile.listener.OnWordSetsListItemViewListener;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.view.WordSetsListItemViewView;

public class WordSetsListItemViewPresenterImpl implements OnWordSetsListItemViewListener, WordSetsListItemViewPresenter {
    private final WordSetsListItemViewView view;
    private final WordSetsListItemViewInteractor interactor;
    private WordSet wordSet;

    public WordSetsListItemViewPresenterImpl(WordSetsListItemViewInteractor interactor, WordSetsListItemViewView view) {
        this.interactor = interactor;
        this.view = view;
    }

    @Override
    public void setModel(WordSet wordSet) {
        this.wordSet = wordSet;
    }

    @Override
    public void refreshModel() {
        interactor.prepareModel(wordSet, this);
    }

    @Override
    public void hideProgress() {
        view.hideProgressBar();
    }

    @Override
    public void showProgress() {
        view.showProgressBar();
    }

    @Override
    public void onModelPrepared(String wordSetRowValue, int progressValue, int availableInHours) {
        view.setWordSetRowValue(wordSetRowValue);
        view.setProgressBarValue(progressValue);
        if (availableInHours == 0) {
            view.hideAvailableInHoursTextView();
            view.enableWordSet();
        } else {
            view.showAvailableInHoursTextView();
            view.setAvailableInHours(availableInHours);
            view.disableWordSet();
        }
        if (progressValue == 0 || progressValue == 100) {
            view.hideProgressBar();
        } else {
            view.showProgressBar();
        }
    }
}