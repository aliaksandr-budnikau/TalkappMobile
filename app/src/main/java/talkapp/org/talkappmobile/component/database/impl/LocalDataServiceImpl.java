package talkapp.org.talkappmobile.component.database.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static java.util.Collections.emptyList;

public class LocalDataServiceImpl implements LocalDataService {
    public static final String TAG = LocalDataServiceImpl.class.getSimpleName();
    private final CollectionType LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE;
    private final WordSetDao wordSetDao;
    private final ObjectMapper mapper;
    private final Logger logger;
    private Map<String, List<WordSet>> allWordSets;

    public LocalDataServiceImpl(WordSetDao wordSetDao, ObjectMapper mapper, Logger logger) {
        this.wordSetDao = wordSetDao;
        this.mapper = mapper;
        this.logger = logger;
        LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, Word2Tokens.class);
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
        return mapping;
    }

    private WordSet toDto(WordSetMapping mapping) {
        WordSet wordSet = new WordSet();
        wordSet.setId(Integer.valueOf(mapping.getId()));
        wordSet.setTopicId(mapping.getTopicId());

        List<Word2Tokens> words;
        try {
            words = mapper.readValue(mapping.getWords(), LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        wordSet.setWords(words);
        return wordSet;
    }
}