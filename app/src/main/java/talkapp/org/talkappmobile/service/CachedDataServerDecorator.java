package talkapp.org.talkappmobile.service;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.impl.InternetConnectionLostException;
import talkapp.org.talkappmobile.service.impl.LocalCacheIsEmptyException;

import static java.util.Arrays.asList;

public class CachedDataServerDecorator extends DataServerDecorator {

    private final LocalDataService localDataService;
    private final WordTranslationService wordTranslationService;

    public CachedDataServerDecorator(DataServer server, LocalDataService localDataService, WordTranslationService wordTranslationService) {
        super(server);
        this.localDataService = localDataService;
        this.wordTranslationService = wordTranslationService;
    }

    @Override
    public Map<String, List<Sentence>> findSentencesByWordSetId(int wordSetId, int wordsNumber) {
        Map<String, List<Sentence>> body = super.findSentencesByWordSetId(wordSetId, wordsNumber);
        if (body != null) {
            localDataService.saveSentences(body, wordsNumber);
        }
        return null;
    }

    @Override
    public List<Topic> findAllTopics() {
        List<Topic> allTopics;
        try {
            allTopics = super.findAllTopics();
        } catch (InternetConnectionLostException e) {
            return localDataService.findAllTopics();
        }
        if (allTopics == null) {
            return new LinkedList<>();
        } else {
            localDataService.saveTopics(allTopics);
        }
        return allTopics;
    }

    @Override
    public List<WordSet> findWordSetsByTopicId(int topicId) {
        List<WordSet> sets = localDataService.findAllWordSetsByTopicId(topicId);
        if (sets == null || sets.isEmpty()) {
            throw new LocalCacheIsEmptyException("WordSets weren't initialized locally");
        }
        return sets;
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordSetIdAndByLanguage(int wordSetId, String language) {
        List<String> words = localDataService.findWordsOfWordSetById(wordSetId);
        List<WordTranslation> translations;
        try {
            translations = super.findWordTranslationsByWordSetIdAndByLanguage(wordSetId, language);
        } catch (InternetConnectionLostException e) {
            try {
                return localDataService.findWordTranslationsByWordsAndByLanguage(words, language);
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
            wordTranslationService.saveWordTranslations(translations);
        }
        return translations;
    }

    @NonNull
    private List<WordTranslation> getWordTranslations(String language, List<String> words) {
        List<WordTranslation> result;
        result = new LinkedList<>();
        for (String word : words) {
            WordTranslation body = null;
            try {
                body = super.findWordTranslationByWordAndByLanguageAndByLetter(word, String.valueOf(word.charAt(0)), language);
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

    private List<WordTranslation> getFromLocalDataStorage(String language, String word) {
        List<WordTranslation> localTranslations = localDataService.findWordTranslationsByWordsAndByLanguage(asList(word), language);
        if (localTranslations.isEmpty()) {
            throw new RuntimeException("It's a bug. Word " + word + " doesn't have translation in the local database.");
        }
        return localTranslations;
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language) {
        return localDataService.findWordTranslationsByWordsAndByLanguage(words, language);
    }

    public DataServer getServer() {
        return super.getServer();
    }
}