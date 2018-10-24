package talkapp.org.talkappmobile.component.impl;

import java.util.List;

import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.model.Sentence;

import static java.util.Arrays.asList;

public class SentenceProviderRepetitionStrategy extends SentenceProviderStrategy {
    private final PracticeWordSetExerciseRepository repository;

    public SentenceProviderRepetitionStrategy(BackendServer server, PracticeWordSetExerciseRepository repository) {
        super(server);
        this.repository = repository;
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(String word, String wordSetId) {
        return asList(repository.findByWordAndWordSetId(word, wordSetId));
    }
}