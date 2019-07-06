package org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Topic;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordTranslation;
import org.talkappmobile.service.DataServer;
import org.talkappmobile.service.GitHubRestClient;
import org.talkappmobile.service.LocalDataService;
import org.talkappmobile.service.SentenceRestClient;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

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
    public void initLocalCacheOfAllSentencesForThisWord(String word, int wordsNumber) {
        Call<List<Sentence>> call = gitHubRestClient.findSentencesByWord(word, wordsNumber);
        List<Sentence> body = null;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            // do nothing
        }
        if (body != null) {
            localDataService.saveSentences(word, body, wordsNumber);
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
        initWordSetIdsOfWord2Tokens(result);
        localDataService.saveWordSets(result);
        return localDataService.findAllWordSets();
    }

    private void initWordSetIdsOfWord2Tokens(LinkedList<WordSet> wordSets) {
        for (WordSet wordSet : wordSets) {
            LinkedList<Word2Tokens> newWords = new LinkedList<>();
            for (Word2Tokens word : wordSet.getWords()) {
                newWords.add(new Word2Tokens(word.getWord(), word.getTokens(), wordSet.getId()));
            }
            wordSet.setWords(newWords);
        }
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
            try {
                return localDataService.findWordTranslationsByWordsAndByLanguage(words, language);
            } catch (InternetConnectionLostException e1) {
                body = getWordTranslations(language, words);
            }
        }
        if (body == null || body.isEmpty()) {
            body = getWordTranslations(language, words);
        }
        if (body.isEmpty()) {
            return new LinkedList<>();
        } else {
            localDataService.saveWordTranslations(body, words, language);
        }
        return body;
    }

    @NonNull
    private List<WordTranslation> getWordTranslations(String language, List<String> words) {
        List<WordTranslation> body;
        body = new LinkedList<>();
        for (String word : words) {
            Call<WordTranslation> callSingleWord = gitHubRestClient.findWordTranslationByWordAndByLanguageAndByLetter(word, String.valueOf(word.charAt(0)), language);
            body.add(requestExecutor.execute(callSingleWord).body());
        }
        return body;
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