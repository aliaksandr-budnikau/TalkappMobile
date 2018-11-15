package talkapp.org.talkappmobile.activity.presenter;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.AllWordSetsInteractor;
import talkapp.org.talkappmobile.activity.listener.OnAllWordSetsListener;
import talkapp.org.talkappmobile.activity.view.AllWordSetsView;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

public class AllWordSetsPresenter implements OnAllWordSetsListener {
    private final Topic topic;
    private final AllWordSetsView view;
    private final AllWordSetsInteractor interactor;

    public AllWordSetsPresenter(Topic topic, AllWordSetsView view, AllWordSetsInteractor interactor) {
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