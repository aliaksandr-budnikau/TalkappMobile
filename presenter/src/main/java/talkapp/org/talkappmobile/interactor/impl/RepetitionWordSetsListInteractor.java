package talkapp.org.talkappmobile.interactor.impl;

import androidx.annotation.NonNull;

import talkapp.org.talkappmobile.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.listener.OnWordSetsListListener;
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
            if (wordSet.getAvailableInHours() == 0) {
                listener.onWordSetNotFinished(topic, wordSet);
            } else {
                listener.onWordSetIsNotAvailableYet(wordSet.getAvailableInHours());
            }
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

    @Override
    public void prepareWordSetDraftForQRCode(int wordSetId, OnWordSetsListListener listener) {

    }
}