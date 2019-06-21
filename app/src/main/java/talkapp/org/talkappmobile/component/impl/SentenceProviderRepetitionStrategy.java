package talkapp.org.talkappmobile.component.impl;

import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public class SentenceProviderRepetitionStrategy extends SentenceProviderStrategy {
    private final WordRepetitionProgressService exerciseService;

    public SentenceProviderRepetitionStrategy(DataServer server, WordRepetitionProgressService exerciseService) {
        super(server);
        this.exerciseService = exerciseService;
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(Word2Tokens word, int wordSetId) {
        return new ArrayList<>(exerciseService.findByWordAndWordSetId(word, word.getSourceWordSetId()));
    }
}