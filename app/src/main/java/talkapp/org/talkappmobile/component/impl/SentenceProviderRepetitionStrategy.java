package talkapp.org.talkappmobile.component.impl;

import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.component.PracticeWordSetExerciseTempRepository;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;

import static java.util.Arrays.asList;

public class SentenceProviderRepetitionStrategy extends SentenceProviderStrategy {

    @Inject
    PracticeWordSetExerciseTempRepository tempRepository;

    public SentenceProviderRepetitionStrategy() {
        DIContext.get().inject(this);
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(String word, String wordSetId) {
        return asList(tempRepository.findByWordAndWordSetId(word, wordSetId));
    }
}