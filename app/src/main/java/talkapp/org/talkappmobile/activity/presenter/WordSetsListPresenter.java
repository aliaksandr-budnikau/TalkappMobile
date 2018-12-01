package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.activity.view.WordSetsListView;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

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
    public void onWordSetFinished(WordSet wordSet) {
        view.onWordSetFinished(wordSet);
    }

    @Override
    public void onResetExperienceClick(WordSetExperience experience) {
        view.onResetExperienceClick(experience);
    }

    @Override
    public void onWordSetNotFinished(Topic topic, WordSet wordSet) {
        view.onWordSetNotFinished(topic, wordSet);
    }

    public void itemClick(WordSet wordSet) {
        interactor.itemClick(topic, wordSet, this);
    }

    public void resetExperienceClick(WordSet wordSet) {
        interactor.resetExperienceClick(wordSet, this);
    }

    public void itemLongClick(WordSet wordSet) {
        view.onItemLongClick(wordSet);
    }
}