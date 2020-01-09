package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;

public class SentenceServiceImpl implements SentenceService {
    public static final int WORDS_NUMBER = 6;
    public final CollectionType LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE;
    private final DataServer server;
    private final SentenceDao sentenceDao;
    private final ObjectMapper mapper;
    private final SentenceMapper sentenceMapper;

    public SentenceServiceImpl(DataServer server, SentenceDao sentenceDao, ObjectMapper mapper) {
        this.server = server;
        this.sentenceDao = sentenceDao;
        this.mapper = mapper;
        this.sentenceMapper = new SentenceMapper(mapper);
        LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, SentenceIdMapping.class);
    }

    @Override
    public boolean classifySentence(Sentence sentence) {
        return server.saveSentenceScore(sentence);
    }

    @Override
    public List<Sentence> fetchSentencesFromServerByWordAndWordSetId(Word2Tokens word) {
        List<Sentence> result = new LinkedList<>(findSentencesByWords(word, WORDS_NUMBER));
        return getRidOfDuplicates(result);
    }

    @NonNull
    private LinkedList<Sentence> getRidOfDuplicates(List<Sentence> sentences) {
        Set<String> texts = new HashSet<>();
        LinkedList<Sentence> result = new LinkedList<>();
        for (Sentence sentence : sentences) {
            if (texts.add(sentence.getTranslations().get("russian"))) {
                result.add(sentence);
            }
        }
        return result;
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

    private List<SentenceIdMapping> getSentenceIdMappings(String sentenceIds) {
        List<SentenceIdMapping> ids;
        try {
            ids = mapper.readValue(sentenceIds, LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return ids;
    }

    private List<Sentence> findSentencesByWords(Word2Tokens words, int wordsNumber) {
        LinkedList<Sentence> result = new LinkedList<>();
        for (SentenceMapping mapping : sentenceDao.findAllByWord(words.getWord(), wordsNumber)) {
            Sentence dto = sentenceMapper.toDto(mapping);
            if (dto.getTokens().size() <= wordsNumber) {
                result.add(dto);
            }
        }
        if (result.isEmpty()) {
            throw new LocalCacheIsEmptyException("Local cache is empty for sentences of this word set");
        }
        return result;
    }
}
