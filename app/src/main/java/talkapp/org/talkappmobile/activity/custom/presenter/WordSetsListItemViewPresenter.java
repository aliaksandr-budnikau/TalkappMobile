package talkapp.org.talkappmobile.activity.custom.presenter;

import talkapp.org.talkappmobile.activity.custom.interactor.WordSetsListItemViewInteractor;
import talkapp.org.talkappmobile.activity.custom.listener.OnWordSetsListItemViewListener;
import talkapp.org.talkappmobile.activity.custom.view.WordSetsListItemViewView;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class WordSetsListItemViewPresenter implements OnWordSetsListItemViewListener {
    private final WordSetsListItemViewView view;
    private final WordSetsListItemViewInteractor interactor;
    private WordSet wordSet;
    private WordSetExperience experience;

    public WordSetsListItemViewPresenter(WordSetsListItemViewInteractor interactor, WordSetsListItemViewView view) {
        this.interactor = interactor;
        this.view = view;
    }

    public void setModel(WordSet wordSet, WordSetExperience experience) {
        this.wordSet = wordSet;
        this.experience = experience;
    }

    public void refreshModel() {
        interactor.prepareModel(wordSet, experience, this);
    }

    public void hideProgress() {
        view.hideProgressBar();
    }

    @Override
    public void onModelPrepared(String wordSetRowValue, int progressValue) {
        view.setWordSetRowValue(wordSetRowValue);
        view.setProgressBarValue(progressValue);
    }
}