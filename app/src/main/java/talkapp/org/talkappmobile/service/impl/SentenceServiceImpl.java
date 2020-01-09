package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.SentenceIdMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import static java.util.Collections.emptyList;

public class SentenceServiceImpl implements SentenceService {
    public static final int WORDS_NUMBER = 6;
    public final CollectionType LINKED_LIST_OF_SENTENCE_ID_JAVA_TYPE;
    private final DataServer server;
    private final WordSetDao wordSetDao;
    private final SentenceDao sentenceDao;
    private final WordRepetitionProgressDao progressDao;
    private final ObjectMapper mapper;
    private final WordSetMapper wordSetMapper;
    private final SentenceMapper sentenceMapper;

    public SentenceServiceImpl(DataServer server, WordSetDao wordSetDao, SentenceDao sentenceDao, WordRepetitionProgressDao progressDao, ObjectMapper mapper) {
        this.server = server;
        this.wordSetDao = wordSetDao;
        this.sentenceDao = sentenceDao;
        this.progressDao = progressDao;
        this.mapper = mapper;
        this.wordSetMapper = new WordSetMapper(mapper);
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

    @Override
    public List<Sentence> fetchSentencesNotFromServerByWordAndWordSetId(Word2Tokens word) {
        return getRidOfDuplicates(new ArrayList<>(findByWordAndWordSetId(word)));
    }

    @Override
    public List<Sentence> findByWordAndWordSetId(Word2Tokens word) {
        WordSetMapping mapping = wordSetDao.findById(word.getSourceWordSetId());
        WordSet wordSet = wordSetMapper.toDto(mapping);
        List<WordRepetitionProgressMapping> exercises = progressDao.findByWordIndexAndWordSetId(wordSet.getWords().indexOf(word), word.getSourceWordSetId());
        if (exercises.isEmpty()) {
            return emptyList();
        }
        WordRepetitionProgressMapping exercise = exercises.get(0);
        if (StringUtils.isEmpty(exercise.getSentenceIds()) || getSentenceIdMappings(exercise.getSentenceIds()).isEmpty()) {
            return emptyList();
        }
        return getSentence(exercise);
    }

    private List<Sentence> getSentence(WordRepetitionProgressMapping exercise) {
        WordSetMapping wordSetMapping = wordSetDao.findById(exercise.getWordSetId());
        WordSet wordSet = wordSetMapper.toDto(wordSetMapping);
        List<SentenceIdMapping> ids = getSentenceIdMappings(exercise.getSentenceIds());
        String[] sentenceIds = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            try {
                SentenceIdMapping idMapping = ids.get(i);
                idMapping.setWord(wordSet.getWords().get(exercise.getWordIndex()).getWord());
                sentenceIds[i] = mapper.writeValueAsString(idMapping);
            } catch (JsonProcessingException e) {
                throw new RuntimeException();
            }
        }
        List<SentenceMapping> sentences = sentenceDao.findAllByIds(sentenceIds);
        if (sentences.isEmpty()) {
            return emptyList();
        }
        Map<String, SentenceMapping> hashMap = new HashMap<>();
        for (SentenceMapping sentence : sentences) {
            hashMap.put(sentence.getId(), sentence);
        }
        LinkedList<Sentence> result = new LinkedList<>();
        for (String id : sentenceIds) {
            SentenceMapping mapping = hashMap.get(id);
            if (mapping != null) {
                result.add(sentenceMapper.toDto(mapping));
            }
        }
        return result;
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

    @NonNull
    private LinkedList<TextToken> getTextTokens(WordTranslation wordTranslation) {
        LinkedList<TextToken> textTokens = new LinkedList<>();
        TextToken textToken = new TextToken();
        textToken.setToken(wordTranslation.getWord());
        textToken.setStartOffset(0);
        textToken.setEndOffset(wordTranslation.getWord().length());
        textToken.setPosition(0);
        textTokens.add(textToken);
        return textTokens;
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
