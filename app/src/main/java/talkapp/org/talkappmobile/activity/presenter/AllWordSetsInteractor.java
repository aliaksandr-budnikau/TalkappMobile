package talkapp.org.talkappmobile.activity.presenter;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.WordSetService;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetExperience;

import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;

public class AllWordSetsInteractor {
    private final WordSetService wordSetService;
    private final WordSetExperienceRepository experienceRepository;
    private final PracticeWordSetExerciseRepository exerciseRepository;
    private final AuthSign authSign;

    public AllWordSetsInteractor(WordSetService wordSetService, WordSetExperienceRepository experienceRepository, PracticeWordSetExerciseRepository exerciseRepository, AuthSign authSign) {
        this.wordSetService = wordSetService;
        this.experienceRepository = experienceRepository;
        this.exerciseRepository = exerciseRepository;
        this.authSign = authSign;
    }

    public void initializeWordSets(String topicId, OnAllWordSetsListener listener) {
        Call<List<WordSet>> wordSetCall;
        if (topicId == null) {
            wordSetCall = wordSetService.findAll(authSign);
        } else {
            wordSetCall = wordSetService.findByTopicId(topicId, authSign);
        }
        Response<List<WordSet>> wordSets;
        try {
            wordSets = wordSetCall.execute();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        listener.onWordSetsInitialized(wordSets.body());
    }

    public void itemClick(WordSet wordSet, OnAllWordSetsListener listener) {
        WordSetExperience experience = experienceRepository.findById(wordSet.getId());
        if (FINISHED.equals(experience.getStatus())) {
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