package talkapp.org.talkappmobile.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.DataServer;

import static java.util.Collections.emptyList;

public class DataServerImpl implements DataServer {
    private final RequestExecutor requestExecutor;
    private final SentenceRestClient sentenceRestClient;
    private final GitHubRestClient gitHubRestClient;

    public DataServerImpl(SentenceRestClient sentenceRestClient, GitHubRestClient gitHubRestClient, RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
        this.sentenceRestClient = sentenceRestClient;
        this.gitHubRestClient = gitHubRestClient;
    }

    @Override
    public Map<String, List<Sentence>> findSentencesByWordSetId(int wordSetId, int wordsNumber) {
        Call<Map<String, List<Sentence>>> call = gitHubRestClient.findSentencesByWordSetId(wordSetId, wordsNumber);
        return requestExecutor.execute(call).body();
    }

    @Override
    public List<Sentence> findSentencesByWord(String word, int wordsNumber) {
        Call<List<Sentence>> call = gitHubRestClient.findSentencesByWord(word, wordsNumber);
        return requestExecutor.execute(call).body();
    }

    @Override
    public List<Topic> findAllTopics() {
        Call<List<Topic>> call = gitHubRestClient.findAllTopics();
        return requestExecutor.execute(call).body();
    }

    @Override
    public List<WordSet> findAllWordSets() {
        Call<Map<Integer, List<WordSet>>> call = gitHubRestClient.findAllWordSets();
        Map<Integer, List<WordSet>> body = requestExecutor.execute(call).body();
        if (body == null) {
            return new LinkedList<>();
        }
        LinkedList<WordSet> result = new LinkedList<>();
        for (List<WordSet> wordSets : body.values()) {
            result.addAll(wordSets);
        }
        return result;
    }

    @Override
    public List<WordSet> findWordSetsByTopicId(int topicId) {
        return emptyList();
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordSetIdAndByLanguage(int wordSetId, String language) {
        Call<List<WordTranslation>> call = gitHubRestClient.findWordTranslationsByWordSetIdAndByLanguage(wordSetId, language);
        return requestExecutor.execute(call).body();
    }

    @Override
    public WordTranslation findWordTranslationByWordAndByLanguageAndByLetter(String word, String letter, String language) {
        Call<WordTranslation> callSingleWord = gitHubRestClient.findWordTranslationByWordAndByLanguageAndByLetter(word, letter, language);
        return requestExecutor.execute(callSingleWord).body();
    }

    @Override
    public WordTranslation findWordTranslationsByWordAndByLanguage(String language, String word) {
        Call<WordTranslation> callSingleWord = gitHubRestClient.findWordTranslationByWordAndByLanguageAndByLetter(word, String.valueOf(word.charAt(0)), language);
        WordTranslation body = requestExecutor.execute(callSingleWord).body();
        if (body != null) {
            body.setWord(body.getWord() == null ? null : body.getWord().toLowerCase());
            body.setTokens(body.getTokens() == null ? null : body.getTokens().toLowerCase());
        }
        return body;
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language) {
        return emptyList();
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