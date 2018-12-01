package talkapp.org.talkappmobile.activity.interactor.impl;

import java.util.List;

import talkapp.org.talkappmobile.activity.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.activity.listener.OnWordSetsListListener;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;

public class StudyingWordSetsListInteractor implements WordSetsListInteractor {
    private final BackendServer server;
    private final WordSetExperienceService experienceService;
    private final PracticeWordSetExerciseService exerciseService;

    public StudyingWordSetsListInteractor(BackendServer server, WordSetExperienceService experienceService, PracticeWordSetExerciseService exerciseService) {
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
            wordSets = server.findWordSetsByTopicId(topic.getId());
        }
        listener.onWordSetsInitialized(wordSets);
    }

    @Override
    public void itemClick(Topic topic, WordSet wordSet, OnWordSetsListListener listener) {
        WordSetExperience experience = experienceService.findById(wordSet.getId());
        if (experience != null && FINISHED.equals(experience.getStatus())) {
            listener.onWordSetFinished(wordSet);
        } else {
            listener.onWordSetNotFinished(topic, wordSet);
        }
    }

    @Override
    public void resetExperienceClick(WordSet wordSet, OnWordSetsListListener listener) {
        exerciseService.cleanByWordSetId(wordSet.getId());
        WordSetExperience experience = experienceService.createNew(wordSet);
        listener.onResetExperienceClick(experience);
    }
}