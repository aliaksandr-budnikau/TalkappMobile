package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.TopicMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.LocalDataService;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;
import talkapp.org.talkappmobile.service.mapper.WordTranslationMapper;

public class LocalDataServiceImpl implements LocalDataService {
    public static final String TAG = LocalDataServiceImpl.class.getSimpleName();
    private final WordSetDao wordSetDao;
    private final TopicDao topicDao;
    private final SentenceDao sentenceDao;
    private final WordTranslationDao wordTranslationDao;
    private final ObjectMapper mapper;
    private final Logger logger;
    private final SentenceMapper sentenceMapper;
    private final WordSetMapper wordSetMapper;
    private final WordTranslationMapper wordTranslationMapper;

    public LocalDataServiceImpl(WordSetDao wordSetDao, TopicDao topicDao, SentenceDao sentenceDao, WordTranslationDao wordTranslationDao, ObjectMapper mapper, Logger logger) {
        this.wordSetDao = wordSetDao;
        this.topicDao = topicDao;
        this.sentenceDao = sentenceDao;
        this.wordTranslationDao = wordTranslationDao;
        this.mapper = mapper;
        this.logger = logger;
        this.sentenceMapper = new SentenceMapper(mapper);
        this.wordSetMapper = new WordSetMapper(mapper);
        this.wordTranslationMapper = new WordTranslationMapper(mapper);
    }

    @Override
    public List<WordSet> findAllWordSetsByTopicId(int topicId) {
        List<WordSetMapping> allMappings = wordSetDao.findAllByTopicId(String.valueOf(topicId));
        List<WordSet> result = new LinkedList<>();
        for (WordSetMapping mapping : allMappings) {
            result.add(wordSetMapper.toDto(mapping));
        }
        return result;
    }

    @Override
    public void saveTopics(final List<Topic> topics) {
        LinkedList<TopicMapping> mappings = new LinkedList<>();
        for (Topic topic : topics) {
            mappings.add(toMapping(topic));
        }
        topicDao.save(mappings);
    }

    @Override
    public List<Topic> findAllTopics() {
        LinkedList<Topic> result = new LinkedList<>();
        for (TopicMapping mapping : topicDao.findAll()) {
            result.add(toDto(mapping));
        }
        return result;
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language) {
        LinkedList<WordTranslation> result = new LinkedList<>();
        for (String word : words) {
            WordTranslationMapping mapping = wordTranslationDao.findByWordAndByLanguage(word, language);
            if (mapping == null) {
                throw new LocalCacheIsEmptyException("Local cache is empty. You need internet connection to fill it.");
            }
            result.add(wordTranslationMapper.toDto(mapping));
        }
        return result;
    }

    @Override
    public List<String> findWordsOfWordSetById(int wordSetId) {
        String wordSetIdString = String.valueOf(wordSetId);
        for (WordSetMapping mapping : wordSetDao.findAll()) {
            if (mapping.getId().equals(wordSetIdString)) {
                LinkedList<String> result = new LinkedList<>();
                List<Word2Tokens> tokens;
                try {
                    tokens = mapper.readValue(mapping.getWords(), wordSetMapper.LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                for (Word2Tokens word : tokens) {
                    result.add(word.getWord());
                }
                return result;
            }
        }
        return new LinkedList<>();
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

    private TopicMapping toMapping(Topic topic) {
        TopicMapping mapping = new TopicMapping();
        mapping.setId(topic.getId());
        mapping.setName(topic.getName());
        return mapping;
    }

    private Topic toDto(TopicMapping mapping) {
        Topic topic = new Topic();
        topic.setId(mapping.getId());
        topic.setName(mapping.getName());
        return topic;
    }
}