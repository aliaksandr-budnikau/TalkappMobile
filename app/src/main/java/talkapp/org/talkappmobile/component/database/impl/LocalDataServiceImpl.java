package talkapp.org.talkappmobile.component.database.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.impl.LocalCacheIsEmptyException;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordTranslationDao;
import talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.TopicMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.WordTranslationMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class LocalDataServiceImpl implements LocalDataService {
    public static final String TAG = LocalDataServiceImpl.class.getSimpleName();
    private final MapType HASH_MAP_OF_STRING_2_STRING_JAVA_TYPE;
    private final CollectionType LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE;
    private final CollectionType LINKED_LIST_OF_TOKENS_JAVA_TYPE;
    private final WordSetDao wordSetDao;
    private final TopicDao topicDao;
    private final SentenceDao sentenceDao;
    private final WordTranslationDao wordTranslationDao;
    private final ObjectMapper mapper;
    private final Logger logger;

    public LocalDataServiceImpl(WordSetDao wordSetDao, TopicDao topicDao, SentenceDao sentenceDao, WordTranslationDao wordTranslationDao, ObjectMapper mapper, Logger logger) {
        this.wordSetDao = wordSetDao;
        this.topicDao = topicDao;
        this.sentenceDao = sentenceDao;
        this.wordTranslationDao = wordTranslationDao;
        this.mapper = mapper;
        this.logger = logger;
        LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, Word2Tokens.class);
        HASH_MAP_OF_STRING_2_STRING_JAVA_TYPE = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, String.class);
        LINKED_LIST_OF_TOKENS_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, TextToken.class);
    }

    @Override
    public List<WordSet> findAllWordSets() {
        List<WordSetMapping> allMappings = wordSetDao.findAll();
        List<WordSet> result = new LinkedList<>();
        for (WordSetMapping mapping : allMappings) {
            result.add(toDto(mapping));
        }
        return result;
    }

    @Override
    public void saveWordSets(final List<WordSet> incomingSets) {
        LinkedList<WordSetMapping> mappingsForSaving = new LinkedList<>();
        for (WordSet wordSet : incomingSets) {
            mappingsForSaving.add(toMapping(wordSet));
        }
        wordSetDao.save(mappingsForSaving);
    }

    @Override
    public List<WordSet> findAllWordSetsByTopicId(int topicId) {
        List<WordSetMapping> allMappings = wordSetDao.findAllByTopicId(String.valueOf(topicId));
        List<WordSet> result = new LinkedList<>();
        for (WordSetMapping mapping : allMappings) {
            result.add(toDto(mapping));
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
            Sentence dto = toDto(mapping);
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
                    tokens = mapper.readValue(mapping.getWords(), LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE);
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
                mappings.add(toMapping(sentence, word, wordsNumber));
            }
            sentenceDao.save(mappings);
        }
    }

    private WordSetMapping toMapping(WordSet wordSet) {
        WordSetMapping mapping = new WordSetMapping();
        mapping.setId(String.valueOf(wordSet.getId()));

        try {
            mapping.setWords(mapper.writeValueAsString(wordSet.getWords()));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        mapping.setTopicId(wordSet.getTopicId());
        mapping.setTop(wordSet.getTop());
        return mapping;
    }

    private WordSet toDto(WordSetMapping mapping) {
        WordSet wordSet = new WordSet();
        wordSet.setId(Integer.valueOf(mapping.getId()));
        wordSet.setTopicId(mapping.getTopicId());
        wordSet.setTop(mapping.getTop());

        List<Word2Tokens> words;
        try {
            words = mapper.readValue(mapping.getWords(), LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        wordSet.setWords(words);
        return wordSet;
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

    private SentenceMapping toMapping(Sentence sentence, String word, int wordsNumber) {
        SentenceMapping mapping = new SentenceMapping();
        mapping.setId(sentence.getId() + "#" + word + "#" + wordsNumber);
        mapping.setText(sentence.getText());
        mapping.setContentScore(sentence.getContentScore());
        try {
            mapping.setTranslations(mapper.writeValueAsString(sentence.getTranslations()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            mapping.setTokens(mapper.writeValueAsString(sentence.getTokens()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return mapping;
    }

    private Sentence toDto(SentenceMapping mapping) {
        Sentence sentence = new Sentence();
        sentence.setId(mapping.getId().split("#")[1]);
        sentence.setText(mapping.getText());
        sentence.setContentScore(mapping.getContentScore());
        Map<String, String> translation = null;
        try {
            translation = mapper.readValue(mapping.getTranslations(), HASH_MAP_OF_STRING_2_STRING_JAVA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        sentence.setTranslations(translation);

        List<TextToken> tokens = null;
        try {
            tokens = mapper.readValue(mapping.getTokens(), LINKED_LIST_OF_TOKENS_JAVA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        sentence.setTokens(tokens);
        return sentence;
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