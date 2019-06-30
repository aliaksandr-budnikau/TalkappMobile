package org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.talkappmobile.dao.SentenceDao;
import org.talkappmobile.dao.TopicDao;
import org.talkappmobile.dao.WordSetDao;
import org.talkappmobile.dao.WordTranslationDao;
import org.talkappmobile.mappings.SentenceMapping;
import org.talkappmobile.mappings.TopicMapping;
import org.talkappmobile.mappings.WordSetMapping;
import org.talkappmobile.mappings.WordTranslationMapping;
import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Topic;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordTranslation;
import org.talkappmobile.service.LocalDataService;
import org.talkappmobile.service.Logger;
import org.talkappmobile.service.mapper.SentenceMapper;
import org.talkappmobile.service.mapper.WordSetMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public LocalDataServiceImpl(WordSetDao wordSetDao, TopicDao topicDao, SentenceDao sentenceDao, WordTranslationDao wordTranslationDao, ObjectMapper mapper, Logger logger) {
        this.wordSetDao = wordSetDao;
        this.topicDao = topicDao;
        this.sentenceDao = sentenceDao;
        this.wordTranslationDao = wordTranslationDao;
        this.mapper = mapper;
        this.logger = logger;
        this.sentenceMapper = new SentenceMapper(mapper);
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    @Override
    public List<WordSet> findAllWordSets() {
        List<WordSetMapping> allMappings = wordSetDao.findAll();
        List<WordSet> result = new LinkedList<>();
        for (WordSetMapping mapping : allMappings) {
            result.add(wordSetMapper.toDto(mapping));
        }
        return result;
    }

    @Override
    public void saveWordSets(final List<WordSet> incomingSets) {
        LinkedList<WordSetMapping> mappingsForSaving = new LinkedList<>();
        for (WordSet wordSet : incomingSets) {
            HashSet<Word2Tokens> setOfWords = new HashSet<>(wordSet.getWords());
            wordSet.setWords(new LinkedList<>(setOfWords));
            WordSetMapping newSet = wordSetMapper.toMapping(wordSet);
            WordSetMapping old = wordSetDao.findById(wordSet.getId());
            if (old != null) {
                newSet.setStatus(old.getStatus());
                newSet.setTrainingExperience(old.getTrainingExperience());
            }
            mappingsForSaving.add(newSet);
        }
        wordSetDao.refreshAll(mappingsForSaving);
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
    public List<Sentence> findSentencesByWords(Word2Tokens words, int wordsNumber) {
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

    @Override
    public List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language) {
        LinkedList<WordTranslation> result = new LinkedList<>();
        for (String word : words) {
            WordTranslationMapping mapping = wordTranslationDao.findByWordAndByLanguage(word, language);
            if (mapping == null) {
                throw new LocalCacheIsEmptyException("Local cache is empty. You need internet connection to fill it.");
            }
            result.add(toDto(mapping));
        }
        return result;
    }

    @Override
    public void saveWordTranslations(final List<WordTranslation> wordTranslations, final List<String> words, String language) {
        List<WordTranslationMapping> mappings = new LinkedList<>();
        for (WordTranslation wordTranslation : wordTranslations) {
            mappings.add(toMapping(wordTranslation));
        }
        wordTranslationDao.save(mappings);
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

    @Override
    public void saveSentences(String word, List<Sentence> sentences, int wordsNumber) {
        LinkedList<SentenceMapping> mappings = new LinkedList<>();
        for (Sentence sentence : sentences) {
            mappings.add(sentenceMapper.toMapping(sentence, word, wordsNumber));
        }
        sentenceDao.save(mappings);
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

    private WordTranslationMapping toMapping(WordTranslation translation) {
        WordTranslationMapping mapping = new WordTranslationMapping();
        mapping.setWord(translation.getWord() + "_" + translation.getLanguage());
        mapping.setTranslation(translation.getTranslation());
        mapping.setLanguage(translation.getLanguage());
        mapping.setTop(translation.getTop());
        return mapping;
    }

    private WordTranslation toDto(WordTranslationMapping mapping) {
        WordTranslation translation = new WordTranslation();
        translation.setWord(mapping.getWord().split("_")[0]);
        translation.setTranslation(mapping.getTranslation());
        translation.setLanguage(mapping.getLanguage());
        translation.setTop(mapping.getTop());
        return translation;
    }
}