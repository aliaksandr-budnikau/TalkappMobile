package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class AllWordSetsPresenter implements OnAllWordSetsListener {
    private final String topicId;
    private final AllWordSetsView view;
    @Inject
    AllWordSetsInteractor interactor;

    public AllWordSetsPresenter(String topicId, AllWordSetsView view) {
        this.topicId = topicId;
        this.view = view;
    }

    public void initialize() {
        interactor.initializeWordSets(topicId, this);
    }

    @Override
    public void onWordSetsInitialized(List<WordSet> wordSets) {
        view.onWordSetsInitialized(wordSets);
    }

    @Override
    public void onWordSetFinished(WordSet wordSet) {
        view.onWordSetFinished(wordSet);
    }

    @Override
    public void onResetExperienceClick(WordSetExperience experience) {
        view.onResetExperienceClick(experience);
    }

    @Override
    public void onWordSetNotFinished(WordSet wordSet) {
        view.onWordSetNotFinished(wordSet);
    }

    public void itemClick(WordSet wordSet) {
        interactor.itemClick(wordSet, this);
    }

    public void resetExperienceClick(WordSet wordSet) {
        interactor.resetExperienceClick(wordSet, this);
    }

    public void itemLongClick(WordSet wordSet) {
        view.onItemLongClick(wordSet);
    }
}