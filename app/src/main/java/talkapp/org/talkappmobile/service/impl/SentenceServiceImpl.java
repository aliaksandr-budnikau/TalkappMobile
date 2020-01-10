package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;

public class SentenceServiceImpl implements SentenceService {
    public final CollectionType LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE;
    private final DataServer server;
    private final SentenceDao sentenceDao;
    private final SentenceMapper sentenceMapper;

    public SentenceServiceImpl(DataServer server, SentenceDao sentenceDao, ObjectMapper mapper) {
        this.server = server;
        this.sentenceDao = sentenceDao;
        this.sentenceMapper = new SentenceMapper(mapper);
        LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, SentenceIdMapping.class);
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
                mappings.add(sentenceMapper.toMapping(sentence, word, wordsNumber));
            }
            sentenceDao.save(mappings);
        }
    }

    @Override
    public Map<String, List<Sentence>> findSentencesByWordSetId(int wordSetId, int wordsNumber) {
        return server.findSentencesByWordSetId(wordSetId, wordsNumber);
    }
}
