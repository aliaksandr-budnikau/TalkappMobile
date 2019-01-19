package talkapp.org.talkappmobile.component.backend.impl;

import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.AccountRestClient;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.GitHubSentenceRestClient;
import talkapp.org.talkappmobile.component.backend.LoginRestClient;
import talkapp.org.talkappmobile.component.backend.SentenceRestClient;
import talkapp.org.talkappmobile.component.backend.TextGrammarCheckRestClient;
import talkapp.org.talkappmobile.component.backend.TopicRestClient;
import talkapp.org.talkappmobile.component.backend.WordSetRestClient;
import talkapp.org.talkappmobile.component.backend.WordTranslationRestClient;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.model.Account;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.LoginCredentials;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

import static talkapp.org.talkappmobile.component.AuthSign.AUTHORIZATION_HEADER_KEY;

public class DataServerImpl implements DataServer {

    private static final String TAG = DataServerImpl.class.getSimpleName();

    private final AuthSign authSign;

    private final AccountRestClient accountRestClient;

    private final LoginRestClient loginRestClient;

    private final SentenceRestClient sentenceRestClient;

    private final TextGrammarCheckRestClient textGrammarCheckRestClient;

    private final TopicRestClient topicRestClient;

    private final WordSetRestClient wordSetRestClient;

    private final WordTranslationRestClient wordTranslationRestClient;

    private final Logger logger;

    private final LocalDataService localDataService;
    private final RequestExecutor requestExecutor;
    private final GitHubSentenceRestClient gitHubSentenceRestClient;

    public DataServerImpl(Logger logger, AuthSign authSign, AccountRestClient accountRestClient, LoginRestClient loginRestClient, SentenceRestClient sentenceRestClient, GitHubSentenceRestClient gitHubSentenceRestClient, TextGrammarCheckRestClient textGrammarCheckRestClient, TopicRestClient topicRestClient, WordSetRestClient wordSetRestClient, WordTranslationRestClient wordTranslationRestClient, LocalDataService localDataService, RequestExecutor requestExecutor) {
        this.logger = logger;
        this.authSign = authSign;
        this.accountRestClient = accountRestClient;
        this.loginRestClient = loginRestClient;
        this.sentenceRestClient = sentenceRestClient;
        this.gitHubSentenceRestClient = gitHubSentenceRestClient;
        this.textGrammarCheckRestClient = textGrammarCheckRestClient;
        this.topicRestClient = topicRestClient;
        this.wordSetRestClient = wordSetRestClient;
        this.wordTranslationRestClient = wordTranslationRestClient;
        this.localDataService = localDataService;
        this.requestExecutor = requestExecutor;
    }

    @Override
    public void registerAccount(Account account) throws RegistrationException {
        Call<Void> call = accountRestClient.register(account);
        Response<Void> response = requestExecutor.execute(call);
        if (response.code() == HttpURLConnection.HTTP_MOVED_TEMP) {
            throw new RegistrationException(response.message());
        }
    }

    @Override
    public String loginUser(LoginCredentials credentials) throws LoginException {
        Call<Boolean> call = loginRestClient.login(credentials);
        Response<Boolean> response = requestExecutor.execute(call);
        Boolean result = response.body();
        String signature = response.headers().get(AUTHORIZATION_HEADER_KEY);
        if (result != null && signature != null && result) {
            authSign.put(signature);
        } else {
            throw new LoginException(response.message());
        }
        return signature;
    }

    @Override
    public List<Sentence> findSentencesByWords(Word2Tokens words, int wordsNumber, int wordSetId) {
        List<Sentence> cached = localDataService.findSentencesByWordsFromMemCache(words, wordsNumber);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        initLocalCacheOfAllSentencesForThisWordset(wordSetId, wordsNumber);
        Call<List<Sentence>> call = sentenceRestClient.findByWords(words.getWord(), wordsNumber, authSign);
        List<Sentence> body = null;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            return localDataService.findSentencesByWords(words, wordsNumber);
        }
        if (body == null) {
            return new LinkedList<>();
        } else {
            localDataService.saveSentences(body, words, wordsNumber);
        }
        return body;
    }

    private void initLocalCacheOfAllSentencesForThisWordset(int wordSetId, int wordsNumber) {
        Call<Map<String, List<Sentence>>> call = gitHubSentenceRestClient.findByWordSetId(wordSetId, wordsNumber);
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
    public List<GrammarError> checkText(String text) {
        Call<List<GrammarError>> call = textGrammarCheckRestClient.check(text, authSign);
        List<GrammarError> body = null;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            // do nothing
        }
        if (body == null) {
            return new LinkedList<>();
        }
        return body;
    }

    @Override
    public List<Topic> findAllTopics() {
        List<Topic> cached = localDataService.findAllTopicsFromMemCache();
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        Call<List<Topic>> call = topicRestClient.findAll(authSign);
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
        Call<List<WordSet>> call = wordSetRestClient.findAll(authSign);
        List<WordSet> body;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            return localDataService.findAllWordSets();
        }
        if (body == null) {
            return new LinkedList<>();
        } else {
            saveAsync(body);
        }
        return body;
    }

    private void saveAsync(final List<WordSet> body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                localDataService.saveWordSets(body);
            }
        }).start();
    }

    @Override
    public List<WordSet> findWordSetsByTopicId(int topicId) {
        List<WordSet> cached = localDataService.findAllWordSetsByTopicId(topicId);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        initLocalCache();
        Call<List<WordSet>> call = wordSetRestClient.findByTopicId(topicId, authSign);
        List<WordSet> body;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            return localDataService.findAllWordSetsByTopicId(topicId);
        }
        if (body == null) {
            return new LinkedList<>();
        }
        return body;
    }

    private void initLocalCache() {
        findAllWordSets();
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordSetIdAndByLanguage(int wordSetId, String language) {
        List<String> words = localDataService.findWordsOfWordSetById(wordSetId);
        List<WordTranslation> cached = localDataService.findWordTranslationsByWordsAndByLanguageMemCache(words, language);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        Call<List<WordTranslation>> call = wordTranslationRestClient.findByWordSetIdAndByLanguage(wordSetId, language, authSign);
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
        List<WordTranslation> cached = localDataService.findWordTranslationsByWordsAndByLanguageMemCache(words, language);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }
        Call<List<WordTranslation>> call = wordTranslationRestClient.findByWordsAndByLanguage(words, language, authSign);
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
    public boolean saveSentenceScore(Sentence sentence) {
        Call<Boolean> call = sentenceRestClient.saveSentenceScore(sentence, authSign);
        Boolean body = null;
        try {
            body = requestExecutor.execute(call).body();
        } catch (InternetConnectionLostException e) {
            return false;
        }
        return body != null;
    }
}