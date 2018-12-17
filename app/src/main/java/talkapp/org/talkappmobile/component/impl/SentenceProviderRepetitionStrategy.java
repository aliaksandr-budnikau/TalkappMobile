package talkapp.org.talkappmobile.component.impl;

import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

import static java.util.Collections.singletonList;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;

public class SentenceProviderRepetitionStrategy extends SentenceProviderStrategy {
    private final PracticeWordSetExerciseService exerciseService;

    public SentenceProviderRepetitionStrategy(BackendServer server, PracticeWordSetExerciseService exerciseService) {
        super(server);
        this.exerciseService = exerciseService;
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        if (wordSetId == 0) {
            return exerciseService.findByWordAndByStatus(word, FINISHED);
        }
        return new ArrayList<>(singletonList(exerciseService.findByWordAndWordSetId(word, wordSetId)));
    }
}