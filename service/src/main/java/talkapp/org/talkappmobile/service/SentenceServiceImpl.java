package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.repository.SentenceRepository;
import talkapp.org.talkappmobile.service.SentenceService;

public class SentenceServiceImpl implements SentenceService {
    private final DataServer server;
    private final SentenceRepository sentenceRepository;

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