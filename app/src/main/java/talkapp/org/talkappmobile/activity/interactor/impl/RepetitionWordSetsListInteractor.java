package talkapp.org.talkappmobile.activity.interactor.impl;

import org.talkappmobile.model.Topic;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.service.WordRepetitionProgressService;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;

public class RepetitionWordSetsListInteractor implements WordSetsListInteractor {
    private final WordRepetitionProgressService exerciseService;

    public RepetitionWordSetsListInteractor(WordRepetitionProgressService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @Override
    public void initializeWordSets(Topic topic, OnWordSetsListListener listener) {
        List<WordSet> wordSets = exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2);
        listener.onWordSetsInitialized(wordSets);
    }

    @Override
    public void itemClick(Topic topic, WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
        int maxWordSetSize = exerciseService.getMaxWordSetSize();
        if (wordSet.getWords().size() == maxWordSetSize) {
            listener.onWordSetNotFinished(topic, wordSet);
        } else {
            listener.onWordSetTooSmallForRepetition(maxWordSetSize, wordSet.getWords().size());
        }
    }

    @Override
    public void resetExperienceClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
    }

    @Override
    public void deleteWordSetClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
    }

    @Override
    public void itemLongClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
    }
}