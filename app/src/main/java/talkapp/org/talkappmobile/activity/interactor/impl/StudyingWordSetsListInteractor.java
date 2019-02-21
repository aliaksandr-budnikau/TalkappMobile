package talkapp.org.talkappmobile.activity.interactor.impl;

import java.util.Collections;
import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.WordSetService;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;

public class StudyingWordSetsListInteractor implements WordSetsListInteractor {
    private final DataServer server;
    private final WordSetService experienceService;
    private final PracticeWordSetExerciseService exerciseService;

    public StudyingWordSetsListInteractor(DataServer server, WordSetService experienceService, PracticeWordSetExerciseService exerciseService) {
        this.server = server;
        this.experienceService = experienceService;
        this.exerciseService = exerciseService;
    }

    @Override
    public void initializeWordSets(Topic topic, OnWordSetsListListener listener) {
        List<WordSet> wordSets;
        if (topic == null) {
            wordSets = server.findAllWordSets();
        } else {
            try {
                wordSets = server.findWordSetsByTopicId(topic.getId());
            } catch (LocalCacheIsEmptyException e) {
                initLocalCache();
                wordSets = server.findWordSetsByTopicId(topic.getId());
            }
        }
        Collections.sort(wordSets, new WordSetComparator());
        listener.onWordSetsInitialized(wordSets);
    }

    private void initLocalCache() {
        server.findAllWordSets();
    }

    @Override
    public void itemClick(Topic topic, WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
        if (FINISHED.equals(wordSet.getStatus())) {
            listener.onWordSetFinished(wordSet, clickedItemNumber);
        } else {
            listener.onWordSetNotFinished(topic, wordSet);
        }
    }

    @Override
    public void resetExperienceClick(WordSet wordSet, int clickedItemNumber, OnWordSetsListListener listener) {
        exerciseService.cleanByWordSetId(wordSet.getId());
        experienceService.createNew(wordSet);
        listener.onResetExperienceClick(wordSet, clickedItemNumber);
    }
}