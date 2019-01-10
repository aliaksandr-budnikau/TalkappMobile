package talkapp.org.talkappmobile.component.database.impl;

import android.support.annotation.NonNull;

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

import static java.util.Collections.emptyList;

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
    private Map<String, List<WordSet>> allWordSets;
    private Map<String, List<Sentence>> allSentences = new HashMap<>();
    private Map<String, WordTranslation> allWordTranslations = new HashMap<>();
    private List<Topic> allTopics;

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
        if (allWordSets != null && !allWordSets.isEmpty()) {
            return getAllWordSets(allWordSets);
        }
        List<WordSetMapping> allMappings = wordSetDao.findAll();
        allWordSets = splitAllWortSetsByTopicId(allMappings);
        return getAllWordSets(allWordSets);
    }

    private Map<String, List<WordSet>> splitAllWortSetsByTopicId(List<WordSetMapping> allMappings) {
        Map<String, List<WordSet>> allWordSets = new HashMap<>();
        for (WordSetMapping mapping : allMappings) {
            List<WordSet> wordSetList = allWordSets.get(mapping.getTopicId());
            if (wordSetList == null) {
                wordSetList = new LinkedList<>();
                allWordSets.put(mapping.getTopicId(), wordSetList);
            }
            wordSetList.add(toDto(mapping));
        }
        return allWordSets;
    }

    @NonNull
    private List<WordSet> getAllWordSets(Map<String, List<WordSet>> all) {
        LinkedList<WordSet> result = new LinkedList<>();
        for (List<WordSet> wordSets : all.values()) {
            result.addAll(wordSets);
        }
        return result;
    }

    @Override
    public void saveWordSets(final List<WordSet> wordSets) {
        if (allWordSets != null && !allWordSets.isEmpty()) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LinkedList<WordSetMapping> mappings = new LinkedList<>();
                for (WordSet wordSet : wordSets) {
                    mappings.add(toMapping(wordSet));
                }
                wordSetDao.save(mappings);
                allWordSets = splitAllWortSetsByTopicId(mappings);
            }
        };
        execute(runnable);
    }

    @Override
    public List<WordSet> findAllWordSetsFromMemCache() {
        if (allWordSets == null) {
            return emptyList();
        }
        return getAllWordSets(allWordSets);
    }

    @Override
    public List<WordSet> findAllWordSetsByTopicIdFromMemCache(int topicId) {
        if (allWordSets == null) {
            return emptyList();
        }
        return allWordSets.get(String.valueOf(topicId));
    }

    @Override
    public List<Topic> findAllTopicsFromMemCache() {
        return allTopics;
    }

    @Override
    public void saveTopics(final List<Topic> topics) {
        if (allTopics != null && !allTopics.isEmpty()) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LinkedList<TopicMapping> mappings = new LinkedList<>();
                for (Topic topic : topics) {
                    mappings.add(toMapping(topic));
                }
                topicDao.save(mappings);
                allTopics = topics;
            }
        };
        execute(runnable);
    }

    @Override
    public List<Topic> findAllTopics() {
        if (allTopics != null && !allTopics.isEmpty()) {
            return allTopics;
        }
        LinkedList<Topic> result = new LinkedList<>();
        for (TopicMapping mapping : topicDao.findAll()) {
            result.add(toDto(mapping));
        }
        return result;
    }

    @Override
    public void saveSentences(final List<Sentence> sentences, final Word2Tokens words, final int wordsNumber) {
        List<Sentence> cache = allSentences.get(getKey(words.getWord(), wordsNumber));
        if (cache != null && !cache.isEmpty()) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LinkedList<SentenceMapping> mappings = new LinkedList<>();
                for (Sentence sentence : sentences) {
                    mappings.add(toMapping(sentence, words.getWord(), wordsNumber));
                }
                sentenceDao.save(mappings);
                allSentences.put(getKey(words.getWord(), wordsNumber), sentences);
            }
        };
        execute(runnable);
    }

    @NonNull
    private String getKey(String word, int wordsNumber) {
        return word + "_" + wordsNumber;
    }

    @Override
    public List<Sentence> findSentencesByWords(Word2Tokens words, int wordsNumber) {
        List<Sentence> cached = allSentences.get(getKey(words.getWord(), wordsNumber));
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }

        LinkedList<Sentence> result = new LinkedList<>();
        for (SentenceMapping mapping : sentenceDao.findAllByWord(words.getWord(), wordsNumber)) {
            Sentence dto = toDto(mapping);
            if (dto.getTokens().size() <= wordsNumber) {
                result.add(dto);
            }
        }
        return result;
    }

    @Override
    public List<Sentence> findSentencesByWordsFromMemCache(Word2Tokens word, int wordsNumber) {
        return allSentences.get(word.getWord() + "_" + wordsNumber);
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordsAndByLanguageMemCache(List<String> words, String language) {
        return getCachedWordTranslations(words, language);
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language) {
        List<WordTranslation> cached = getCachedWordTranslations(words, language);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }

        return findAllByWordsAndByLanguage(words, language);
    }

    private List<WordTranslation> findAllByWordsAndByLanguage(List<String> words, String language) {
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

    private List<WordTranslation> getCachedWordTranslations(List<String> words, String language) {
        LinkedList<WordTranslation> result = new LinkedList<>();
        for (String word : words) {
            WordTranslation wordTranslation = allWordTranslations.get(word + "_" + language);
            if (wordTranslation == null) {
                return emptyList();
            }
            result.add(wordTranslation);
        }
        return result;
    }

    @Override
    public void saveWordTranslations(final List<WordTranslation> wordTranslations, final List<String> words, String language) {
        List<WordTranslation> cached = getCachedWordTranslations(words, language);
        if (cached != null && !cached.isEmpty()) {
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<WordTranslationMapping> mappings = new LinkedList<>();
                for (WordTranslation wordTranslation : wordTranslations) {
                    mappings.add(toMapping(wordTranslation));
                }
                wordTranslationDao.save(mappings);
                saveMemCache(wordTranslations);
            }
        };
        execute(runnable);
    }

    @Override
    public List<String> findWordsOfWordSetByIdFromMemCache(int wordSetId) {
        for (WordSet wordSet : getAllWordSets(allWordSets)) {
            if (wordSet.getId() == wordSetId) {
                LinkedList<String> result = new LinkedList<>();
                for (Word2Tokens word : wordSet.getWords()) {
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
            List<Sentence> cache = allSentences.get(getKey(word, wordsNumber));
            if (cache == null || cache.isEmpty()) {
                break;
            }
            return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (String word : words2Sentences.keySet()) {
                    LinkedList<SentenceMapping> mappings = new LinkedList<>();
                    for (Sentence sentence : words2Sentences.get(word)) {
                        mappings.add(toMapping(sentence, word, wordsNumber));
                    }
                    sentenceDao.save(mappings);
                    allSentences.put(getKey(word, wordsNumber), words2Sentences.get(word));
                }
            }
        };
        execute(runnable);
    }

    private void saveMemCache(List<WordTranslation> wordTranslations) {
        for (WordTranslation wordTranslation : wordTranslations) {
            allWordTranslations.put(getKey(wordTranslation.getWord(), wordTranslation.getLanguage()), wordTranslation);
        }
    }

    private String getKey(String word, String language) {
        return word + "_" + language;
    }

    private void execute(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.e(TAG, e.getMessage(), e);
            }
        });
        thread.start();
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