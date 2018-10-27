package talkapp.org.talkappmobile.activity.interactor;

import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnAllWordSetsListener;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;

public class AllWordSetsInteractor {
    private final BackendServer server;
    private final WordSetExperienceRepository experienceRepository;
    private final PracticeWordSetExerciseRepository exerciseRepository;

    public AllWordSetsInteractor(BackendServer server, WordSetExperienceRepository experienceRepository, PracticeWordSetExerciseRepository exerciseRepository) {
        this.server = server;
        this.experienceRepository = experienceRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public void initializeWordSets(int topicId, OnAllWordSetsListener listener) {
        List<WordSet> wordSets;
        if (topicId == -1) {
            wordSets = server.findAllWordSets();
        } else {
            wordSets = server.findWordSetsByTopicId(topicId);
        }
        listener.onWordSetsInitialized(wordSets);
    }

    public void itemClick(WordSet wordSet, OnAllWordSetsListener listener) {
        WordSetExperience experience = experienceRepository.findById(wordSet.getId());
        if (experience != null && FINISHED.equals(experience.getStatus())) {
            listener.onWordSetFinished(wordSet);
        } else {
            listener.onWordSetNotFinished(wordSet);
        }
    }

    public void resetExperienceClick(WordSet wordSet, OnAllWordSetsListener listener) {
        exerciseRepository.cleanByWordSetId(wordSet.getId());
        WordSetExperience experience = experienceRepository.createNew(wordSet);
        listener.onResetExperienceClick(experience);
    }
}