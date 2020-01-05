package talkapp.org.talkappmobile.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.impl.InternetConnectionLostException;

import static java.util.Arrays.asList;

public class CachedDataServerDecorator extends DataServerDecorator {

    private final LocalDataService localDataService;

    public CachedDataServerDecorator(DataServer server, LocalDataService localDataService) {
        super(server);
        this.localDataService = localDataService;
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