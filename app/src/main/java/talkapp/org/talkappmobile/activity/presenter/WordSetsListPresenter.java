package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.activity.view.WordSetsListView;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

public class WordSetsListPresenter implements OnWordSetsListListener {
    private final Topic topic;
    private final WordSetsListView view;
    private final WordSetsListInteractor interactor;

    public WordSetsListPresenter(Topic topic, WordSetsListView view, WordSetsListInteractor interactor) {
        this.topic = topic;
        this.view = view;
        this.interactor = interactor;
    }

    public void initialize() {
        try {
            view.onInitializeBeginning();
            interactor.initializeWordSets(topic, this);
        } finally {
            view.onInitializeEnd();
        }
    }

    @Override
    public void onWordSetsInitialized(List<WordSet> wordSets) {
        view.onWordSetsInitialized(wordSets);
    }

    @Override
    public void onWordSetFinished(WordSet wordSet, int clickedItemNumber) {
        view.onWordSetFinished(wordSet, clickedItemNumber);
    }

    @Override
    public void onResetExperienceClick(WordSet wordSet, int clickedItemNumber) {
        view.onResetExperienceClick(wordSet, clickedItemNumber);
    }

    @Override
    public void onWordSetNotFinished(Topic topic, WordSet wordSet) {
        view.onWordSetNotFinished(topic, wordSet);
    }

    public void itemClick(WordSet wordSet, int clickedItemNumber) {
        interactor.itemClick(topic, wordSet, clickedItemNumber, this);
    }

    public void resetExperienceClick(WordSet wordSet, int clickedItemNumber) {
        interactor.resetExperienceClick(wordSet, clickedItemNumber, this);
    }

    public void itemLongClick(WordSet wordSet, int clickedItemNumber) {
        view.onItemLongClick(wordSet, clickedItemNumber);
    }
}