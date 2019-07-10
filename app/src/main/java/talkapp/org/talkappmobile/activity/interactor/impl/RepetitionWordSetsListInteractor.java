package talkapp.org.talkappmobile.activity.interactor.impl;

import android.support.annotation.NonNull;

import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;

import java.util.List;

public class RepetitionWordSetsListInteractor implements WordSetsListInteractor {
    @NonNull
    private final WordRepetitionProgressService exerciseService;
    @NonNull
    private final RepetitionClass repetitionClass;

    public RepetitionWordSetsListInteractor(@NonNull WordRepetitionProgressService exerciseService, @NonNull RepetitionClass repetitionClass) {
        this.exerciseService = exerciseService;
        this.repetitionClass = repetitionClass;
    }

    @Override
    public void initializeWordSets(Topic topic, OnWordSetsListListener listener) {
        List<WordSet> wordSets = getFinishedWordSetsSortByUpdatedDate();
        listener.onWordSetsInitialized(wordSets, repetitionClass);
    }

    private List<WordSet> getFinishedWordSetsSortByUpdatedDate() {
        return exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2);
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

    @Override
    public void refreshWordSets(Topic topic, OnWordSetsListListener listener) {
        List<WordSet> wordSets = getFinishedWordSetsSortByUpdatedDate();
        listener.onWordSetsFetched(wordSets, repetitionClass);
    }
}