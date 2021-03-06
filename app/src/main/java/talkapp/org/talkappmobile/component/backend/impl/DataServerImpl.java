package talkapp.org.talkappmobile.component.backend.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.GitHubRestClient;
import talkapp.org.talkappmobile.component.backend.SentenceRestClient;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public class DataServerImpl implements DataServer {
    private final SentenceRestClient sentenceRestClient;

    private final LocalDataService localDataService;
    private final RequestExecutor requestExecutor;
    private final GitHubRestClient gitHubRestClient;

    public DataServerImpl(SentenceRestClient sentenceRestClient, GitHubRestClient gitHubRestClient, LocalDataService localDataService, RequestExecutor requestExecutor) {
        this.sentenceRestClient = sentenceRestClient;
        this.gitHubRestClient = gitHubRestClient;
        this.localDataService = localDataService;
        this.requestExecutor = requestExecutor;
    }

    @Override
    public List<Sentence> findSentencesByWords(Word2Tokens words, int wordsNumber, int wordSetId) {
        return localDataService.findSentencesByWords(words, wordsNumber);
    }

    @Override
    public void initLocalCacheOfAllSentencesForThisWordset(int wordSetId, int wordsNumber) {
        Call<Map<String, List<Sentence>>> call = gitHubRestClient.findSentencesByWordSetId(wordSetId, wordsNumber);
        Map<String, List<Sentence>> body = null;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            // do nothing
        }
        if (body != null) {
            localDataService.saveSentences(body, wordsNumber);
        }
    }

    @Override
    public List<Topic> findAllTopics() {
        Call<List<Topic>> call = gitHubRestClient.findAllTopics();
        List<Topic> body = null;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            return localDataService.findAllTopics();
        }
        if (body == null) {
            return new LinkedList<>();
        } else {
            localDataService.saveTopics(body);
        }
        return body;
    }

    @Override
    public List<WordSet> findAllWordSets() {
        Call<Map<Integer, List<WordSet>>> call = gitHubRestClient.findAllWordSets();
        Map<Integer, List<WordSet>> body;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            return localDataService.findAllWordSets();
        }
        if (body == null) {
            return new LinkedList<>();
        }
        LinkedList<WordSet> result = new LinkedList<>();
        for (List<WordSet> wordSets : body.values()) {
            result.addAll(wordSets);
        }
        localDataService.saveWordSets(result);
        return result;
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
        Call<List<WordTranslation>> call = gitHubRestClient.findWordTranslationsByWordSetIdAndByLanguage(wordSetId, language);
        List<WordTranslation> body = null;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            return localDataService.findWordTranslationsByWordsAndByLanguage(words, language);
        }
        if (body == null) {
            return new LinkedList<>();
        } else {
            localDataService.saveWordTranslations(body, words, language);
        }
        return body;
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language) {
        return localDataService.findWordTranslationsByWordsAndByLanguage(words, language);
    }

    @Override
    public boolean saveSentenceScore(Sentence sentence) {
        Call<Boolean> call = sentenceRestClient.saveSentenceScore(sentence);
        Boolean body = null;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            return false;
        }
        return body != null;
    }
}