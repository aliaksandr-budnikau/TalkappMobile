package talkapp.org.talkappmobile.component.database.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public class LocalDataServiceImpl implements LocalDataService {
    public static final String TAG = LocalDataServiceImpl.class.getSimpleName();
    private final CollectionType LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE;
    private final WordSetDao wordSetDao;
    private final ObjectMapper mapper;
    private final Logger logger;
    private List<WordSet> allWordSets;

    public LocalDataServiceImpl(WordSetDao wordSetDao, ObjectMapper mapper, Logger logger) {
        this.wordSetDao = wordSetDao;
        this.mapper = mapper;
        this.logger = logger;
        LINKED_LIST_OF_WORD_2_TOKENS_JAVA_TYPE = mapper.getTypeFactory().constructCollectionType(LinkedList.class, Word2Tokens.class);
    }

    @Override
    public List<WordSet> findAllWordSets() {
        if (allWordSets != null && !allWordSets.isEmpty()) {
            return allWordSets;
        }
        List<WordSetMapping> allMappings = wordSetDao.findAll();
        allWordSets = new LinkedList<>();
        for (WordSetMapping mapping : allMappings) {
            allWordSets.add(toDto(mapping));
        }
        return allWordSets;
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
                allWordSets = wordSets;
            }
        };
        execute(runnable);
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