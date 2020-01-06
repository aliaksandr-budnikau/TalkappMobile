package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;
import talkapp.org.talkappmobile.service.mapper.WordTranslationMapper;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

public class WordTranslationServiceImpl implements WordTranslationService {
    public static final String RUSSIAN_LANGUAGE = "russian";
    private final DataServer server;
    private final WordTranslationDao wordTranslationDao;
    private final WordSetDao wordSetDao;
    private final ObjectMapper mapper;
    private final WordTranslationMapper wordTranslationMapper;
    private final WordSetMapper wordSetMapper;

    public WordTranslationServiceImpl(DataServer server, WordTranslationDao wordTranslationDao, WordSetDao wordSetDao, ObjectMapper mapper) {
        this.server = server;
        this.wordTranslationDao = wordTranslationDao;
        this.wordSetDao = wordSetDao;
        this.mapper = mapper;
        this.wordTranslationMapper = new WordTranslationMapper(mapper);
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    @Override
    public void saveWordTranslations(List<WordTranslation> wordTranslations) {
        List<WordTranslationMapping> mappings = new LinkedList<>();
        for (WordTranslation wordTranslation : wordTranslations) {
            WordTranslationMapping mapping = wordTranslationMapper.toMapping(wordTranslation);
            if (StringUtils.isEmpty(mapping.getId())) {
                mapping.setId(valueOf(System.currentTimeMillis()));
            }
            mappings.add(mapping);
        }
        wordTranslationDao.save(mappings);
    }

    @Override
    public void saveWordTranslations(String phrase, String translation) {
        WordTranslation wordTranslation = new WordTranslation();
        wordTranslation.setLanguage(RUSSIAN_LANGUAGE);
        wordTranslation.setTranslation(translation);
        wordTranslation.setWord(phrase);
        wordTranslation.setTokens(phrase);
        saveWordTranslations(asList(wordTranslation));
    }

    @Override
    public void saveWordTranslations(NewWordSetDraft wordSetDraft) {
        List<WordTranslation> wordTranslations = wordSetDraft.getWordTranslations();
        for (WordTranslation wordTranslation : wordTranslations) {
            saveWordTranslations(wordTranslation.getWord(), wordTranslation.getTranslation());
        }
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

    private List<WordTranslation> getFromLocalDataStorage(String language, String word) {
        List<WordTranslation> localTranslations = findWordTranslationsByWordsAndByLanguage(asList(word), language);
        if (localTranslations.isEmpty()) {
            throw new RuntimeException("It's a bug. Word " + word + " doesn't have translation in the local database.");
        }
        return localTranslations;
    }

    @NonNull
    private List<WordTranslation> getWordTranslations(String language, List<String> words) {
        List<WordTranslation> result;
        result = new LinkedList<>();
        for (String word : words) {
            WordTranslation body = null;
            try {
                body = server.findWordTranslationByWordAndByLanguageAndByLetter(word, String.valueOf(word.charAt(0)), language);
            } catch (InternetConnectionLostException e) {
                result.addAll(getFromLocalDataStorage(language, word));
            }
            if (body == null) {
                result.addAll(getFromLocalDataStorage(language, word));
            } else {
                result.add(body);
            }
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
    public List<WordTranslation> findWordTranslationsByWordSetIdAndByLanguage(int wordSetId, String language) {
        List<String> words = findWordsOfWordSetById(wordSetId);
        List<WordTranslation> translations;
        try {
            translations = server.findWordTranslationsByWordSetIdAndByLanguage(wordSetId, language);
        } catch (InternetConnectionLostException e) {
            try {
                return findWordTranslationsByWordsAndByLanguage(words, language);
            } catch (InternetConnectionLostException e1) {
                translations = getWordTranslations(language, words);
            }
        }
        if (translations == null || translations.isEmpty()) {
            translations = getWordTranslations(language, words);
        }
        if (translations.isEmpty()) {
            return new LinkedList<>();
        } else {
            saveWordTranslations(translations);
        }
        return translations;
    }

    @Override
    public WordTranslation findByWordAndLanguage(String word, String language) {
        WordTranslationMapping translationMapping = wordTranslationDao.findByWordAndByLanguage(word, language);
        if (translationMapping == null) {
            return null;
        }
        return wordTranslationMapper.toDto(translationMapping);
    }

    @Override
    public WordTranslation findWordTranslationsByWordAndByLanguage(String language, String word) {
        return server.findWordTranslationsByWordAndByLanguage(language, word);
    }
}
