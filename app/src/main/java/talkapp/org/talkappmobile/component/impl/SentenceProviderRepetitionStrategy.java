package talkapp.org.talkappmobile.component.impl;

import java.util.List;

import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.backend.SentenceService;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.model.Sentence;

import static java.util.Arrays.asList;

public class SentenceProviderRepetitionStrategy extends SentenceProviderStrategy {
    private final PracticeWordSetExerciseRepository repository;

    public SentenceProviderRepetitionStrategy(SentenceService sentenceService, AuthSign authSign, PracticeWordSetExerciseRepository repository) {
        super(sentenceService, authSign);
        this.repository = repository;
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(String word, String wordSetId) {
        return asList(repository.findByWordAndWordSetId(word, wordSetId));
    }
}