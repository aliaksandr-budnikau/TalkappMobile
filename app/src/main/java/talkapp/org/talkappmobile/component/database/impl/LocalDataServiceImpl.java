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
    private Map<String, List<Sentence>> allSentences = new HashMap<>();
    private Map<String, WordTranslation> allWordTranslations = new HashMap<>();

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
            List<Sentence> cache = allSentences.get(getKey(word, wordsNumber));
            if (cache == null || cache.isEmpty()) {
                break;
            }
            return;
        }
        for (String word : words2Sentences.keySet()) {
            LinkedList<SentenceMapping> mappings = new LinkedList<>();
            for (Sentence sentence : words2Sentences.get(word)) {
                mappings.add(toMapping(sentence, word, wordsNumber));
            }
            sentenceDao.save(mappings);
            allSentences.put(getKey(word, wordsNumber), words2Sentences.get(word));
        }
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