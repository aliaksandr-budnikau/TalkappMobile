package talkapp.org.talkappmobile.activity.interactor.impl;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

public class RepetitionWordSetsListInteractor implements WordSetsListInteractor {
    private final PracticeWordSetExerciseService exerciseService;

    public RepetitionWordSetsListInteractor(PracticeWordSetExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @Override
    public void initializeWordSets(Topic topic, OnWordSetsListListener listener) {
        List<WordSet> wordSets = exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2);
        listener.onWordSetsInitialized(wordSets);
    }

    @Override
    public void itemClick(Topic topic, WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
        listener.onWordSetNotFinished(topic, wordSet);
    }

    @Override
    public void resetExperienceClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
    }
}