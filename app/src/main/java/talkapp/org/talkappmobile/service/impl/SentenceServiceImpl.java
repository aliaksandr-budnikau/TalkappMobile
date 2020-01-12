package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;

public class SentenceServiceImpl implements SentenceService {
    private final DataServer server;
    private final SentenceDao sentenceDao;
    private final SentenceMapper sentenceMapper;

    public SentenceServiceImpl(DataServer server, SentenceDao sentenceDao, ObjectMapper mapper) {
        this.server = server;
        this.sentenceDao = sentenceDao;
        this.sentenceMapper = new SentenceMapper(mapper);
    }

    @Override
    public boolean classifySentence(Sentence sentence) {
        return server.saveSentenceScore(sentence);
    }

    @Override
    public void saveSentences(final Map<String, List<Sentence>> words2Sentences, final int wordsNumber) {
        for (String word : words2Sentences.keySet()) {
            LinkedList<SentenceMapping> mappings = new LinkedList<>();
            for (Sentence sentence : words2Sentences.get(word)) {
                mappings.add(sentenceMapper.toMapping(sentence));
            }
            sentenceDao.save(mappings);
        }
    }
}
