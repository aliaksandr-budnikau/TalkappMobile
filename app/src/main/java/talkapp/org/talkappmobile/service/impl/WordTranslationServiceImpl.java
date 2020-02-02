package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.WordSetRepository;
import talkapp.org.talkappmobile.service.WordTranslationRepository;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static java.util.Arrays.asList;

public class WordTranslationServiceImpl implements WordTranslationService {
    public static final String RUSSIAN_LANGUAGE = "russian";
    private final DataServer server;
    private final WordTranslationRepository wordTranslationRepository;
    private final WordSetRepository wordSetRepository;

    public WordTranslationServiceImpl(DataServer server, WordTranslationRepository wordTranslationRepository, WordSetRepository wordSetRepository) {
        this.server = server;
        this.wordTranslationRepository = wordTranslationRepository;
        this.wordSetRepository = wordSetRepository;
    }

    @Override
    public void saveWordTranslations(List<WordTranslation> wordTranslations) {
        wordTranslationRepository.createNewOrUpdate(wordTranslations);
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
        for (WordSet wordSet : wordSetRepository.findAll()) {
            if (wordSet.getId() != wordSetId) {
                continue;
            }
            LinkedList<String> result = new LinkedList<>();
            for (Word2Tokens word : wordSet.getWords()) {
                result.add(word.getWord());
            }
            return result;
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
            WordTranslation translation = wordTranslationRepository.findByWordAndByLanguage(word, language);
            if (translation == null) {
                throw new LocalCacheIsEmptyException("Local cache is empty. You need internet connection to fill it.");
            }
            result.add(translation);
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
        return wordTranslationRepository.findByWordAndByLanguage(word, language);
    }

    @Override
    public WordTranslation findWordTranslationsByWordAndByLanguage(String language, String word) {
        return server.findWordTranslationsByWordAndByLanguage(language, word);
    }
}
