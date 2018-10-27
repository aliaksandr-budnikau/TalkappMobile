package talkapp.org.talkappmobile.component.backend.impl;

import android.content.Context;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.SaveSharedPreference;
import talkapp.org.talkappmobile.component.backend.AccountRestClient;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.LoginRestClient;
import talkapp.org.talkappmobile.component.backend.SentenceRestClient;
import talkapp.org.talkappmobile.component.backend.TextGrammarCheckRestClient;
import talkapp.org.talkappmobile.component.backend.TopicRestClient;
import talkapp.org.talkappmobile.component.backend.WordSetRestClient;
import talkapp.org.talkappmobile.component.backend.WordTranslationRestClient;
import talkapp.org.talkappmobile.model.Account;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.LoginCredentials;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

import static talkapp.org.talkappmobile.component.AuthSign.AUTHORIZATION_HEADER_KEY;

public class BackendServerImpl implements BackendServer {

    private static final String TAG = BackendServerImpl.class.getSimpleName();

    private final AuthSign authSign;

    private final AccountRestClient accountRestClient;

    private final LoginRestClient loginRestClient;

    private final SentenceRestClient sentenceRestClient;

    private final TextGrammarCheckRestClient textGrammarCheckRestClient;

    private final TopicRestClient topicRestClient;

    private final WordSetRestClient wordSetRestClient;

    private final WordTranslationRestClient wordTranslationRestClient;

    private final SaveSharedPreference saveSharedPreference;

    private final Context context;

    private final Logger logger;

    public BackendServerImpl(Logger logger, AuthSign authSign, Context context, AccountRestClient accountRestClient, LoginRestClient loginRestClient, SentenceRestClient sentenceRestClient, TextGrammarCheckRestClient textGrammarCheckRestClient, TopicRestClient topicRestClient, WordSetRestClient wordSetRestClient, WordTranslationRestClient wordTranslationRestClient, SaveSharedPreference saveSharedPreference) {
        this.logger = logger;
        this.authSign = authSign;
        this.context = context;
        this.accountRestClient = accountRestClient;
        this.loginRestClient = loginRestClient;
        this.sentenceRestClient = sentenceRestClient;
        this.textGrammarCheckRestClient = textGrammarCheckRestClient;
        this.topicRestClient = topicRestClient;
        this.wordSetRestClient = wordSetRestClient;
        this.wordTranslationRestClient = wordTranslationRestClient;
        this.saveSharedPreference = saveSharedPreference;
    }

    @Override
    public void registerAccount(Account account) throws RegistrationException {
        Call<Void> call = accountRestClient.register(account);
        Response<Void> response = execute(call);
        if (response.code() == HttpURLConnection.HTTP_MOVED_TEMP) {
            throw new RegistrationException(response.message());
        }
    }

    @Override
    public boolean loginUser(LoginCredentials credentials) throws LoginException {
        Call<Boolean> call = loginRestClient.login(credentials);
        Response<Boolean> response = execute(call);
        Boolean result = response.body();
        String signature = response.headers().get(AUTHORIZATION_HEADER_KEY);
        if (result != null && signature != null && result) {
            authSign.put(signature);
            saveSharedPreference.setAuthorizationHeaderKey(context, signature);
        } else {
            throw new LoginException(response.message());
        }
        return false;
    }

    @Override
    public List<Sentence> findSentencesByWords(String words, int wordsNumber) {
        Call<List<Sentence>> call = sentenceRestClient.findByWords(words, wordsNumber, authSign);
        List<Sentence> body = execute(call).body();
        if (body == null) {
            return new LinkedList<>();
        }
        return body;
    }

    private <T> Response<T> execute(Call<T> call) {
        try {
            return call.execute();
        } catch (ConnectException e) {
            logger.e(TAG, e, e.getMessage());
            throw new InternetConnectionLostException("Internet connection was lost");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<GrammarError> checkText(String text) {
        Call<List<GrammarError>> call = textGrammarCheckRestClient.check(text, authSign);
        List<GrammarError> body = execute(call).body();
        if (body == null) {
            return new LinkedList<>();
        }
        return body;
    }

    @Override
    public List<Topic> findAllTopics() {
        Call<List<Topic>> call = topicRestClient.findAll(authSign);
        List<Topic> body = execute(call).body();
        if (body == null) {
            return new LinkedList<>();
        }
        return body;
    }

    @Override
    public List<WordSet> findAllWordSets() {
        Call<List<WordSet>> call = wordSetRestClient.findAll(authSign);
        List<WordSet> body = execute(call).body();
        if (body == null) {
            return new LinkedList<>();
        }
        return body;
    }

    @Override
    public List<WordSet> findWordSetsByTopicId(int topicId) {
        Call<List<WordSet>> call = wordSetRestClient.findByTopicId(topicId, authSign);
        List<WordSet> body = execute(call).body();
        if (body == null) {
            return new LinkedList<>();
        }
        return body;
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordSetIdAndByLanguage(String wordSetId, String language) {
        Call<List<WordTranslation>> call = wordTranslationRestClient.findByWordSetIdAndByLanguage(wordSetId, language, authSign);
        List<WordTranslation> body = execute(call).body();
        if (body == null) {
            return new LinkedList<>();
        }
        return body;
    }
}