package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.repository.SentenceRepository;

public class SentenceServiceImpl implements SentenceService {
    private final DataServer server;
    private final SentenceRepository sentenceRepository;

    @Inject
    public SentenceServiceImpl(DataServer server, SentenceRepository sentenceRepository) {
        this.server = server;
        this.sentenceRepository = sentenceRepository;
    }

    @Override
    public boolean classifySentence(Sentence sentence) {
        return server.saveSentenceScore(sentence);
    }

    @Override
    public void saveSentences(final Map<String, List<Sentence>> words2Sentences, final int wordsNumber) {
        for (String word : words2Sentences.keySet()) {
            for (Sentence sentence : words2Sentences.get(word)) {
                sentenceRepository.createNewOrUpdate(sentence);
            }
        }
    }
}