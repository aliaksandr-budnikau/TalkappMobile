package talkapp.org.talkappmobile.activity.custom.presenter;

import talkapp.org.talkappmobile.activity.custom.interactor.WordSetsListItemViewInteractor;
import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetsListItemViewListener;
import talkapp.org.talkappmobile.activity.custom.view.WordSetsListItemViewView;
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