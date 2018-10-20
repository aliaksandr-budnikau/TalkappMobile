package talkapp.org.talkappmobile.component.impl;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.config.DIContextUtils;
import talkapp.org.talkappmobile.model.Sentence;

import static java.util.Arrays.asList;

public class SentenceProviderRepetitionStrategy extends SentenceProviderStrategy {

    @Inject
    PracticeWordSetExerciseRepository repository;

    public SentenceProviderRepetitionStrategy() {
        DIContextUtils.get().inject(this);
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(String word, String wordSetId) {
        return asList(repository.findByWordAndWordSetId(word, wordSetId));
    }
}