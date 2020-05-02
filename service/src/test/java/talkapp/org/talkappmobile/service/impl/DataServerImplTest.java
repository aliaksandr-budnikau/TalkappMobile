package talkapp.org.talkappmobile.service.impl;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.BuildConfig;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.DataServerImpl;
import talkapp.org.talkappmobile.service.GitHubRestClient;
import talkapp.org.talkappmobile.service.InternetConnectionLostException;
import talkapp.org.talkappmobile.service.RequestExecutor;
import talkapp.org.talkappmobile.service.SentenceRestClient;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryImpl;

import static android.os.Build.VERSION_CODES.M;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static talkapp.org.talkappmobile.model.RepetitionClass.SEEN;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {M}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class DataServerImplTest {

    public static final int KEY = 1;
    private RequestExecutor requestExecutor;
    private GitHubRestClient gitHubRestClient;
    private ObjectMapper mapper = new ObjectMapper();
    private ServiceFactory serviceFactory;

    @Before
    public void init() throws SQLException {
        gitHubRestClient = mock(GitHubRestClient.class);
        requestExecutor = mock(RequestExecutor.class);
        serviceFactory = new ServiceFactoryImpl(RuntimeEnvironment.application) {
            @Override
            public synchronized DataServer getDataServer() {
                return new DataServerImpl(mock(SentenceRestClient.class), gitHubRestClient, requestExecutor);
            }
        };
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void findAllWordSets_internetExistsEachTime() throws InterruptedException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        WordSet wordSet1 = new WordSet();
        wordSet1.setWords(new ArrayList<Word2Tokens>());
        wordSet1.setTopicId("topic");
        wordSet1.setRepetitionClass(SEEN);

        WordSet wordSet2 = new WordSet(wordSet1);
        wordSet2.setId(3);
        expectedSets.put(KEY, asList(wordSet1, wordSet2));
        serviceFactory.getWordSetService().saveWordSets(expectedSets.get(KEY));
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);

        // when first connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenReturn(response);
        List<WordSet> actualSets = serviceFactory.getWordSetService().getWordSets(null);
        Thread.sleep(1000);

        // then
        assertEquals(expectedSets.get(KEY), actualSets);


        Mockito.reset(requestExecutor);
        Mockito.reset(gitHubRestClient);

        // when second connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenReturn(response);
        actualSets = serviceFactory.getWordSetService().getWordSets(null);

        // then
        assertEquals(expectedSets.get(KEY), actualSets);


    }

    @Test
    public void findAllWordSets_internetExistsFirstTimeOnly() throws InterruptedException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        WordSet wordSet1 = new WordSet();
        wordSet1.setWords(new ArrayList<Word2Tokens>());
        wordSet1.setTopicId("topic");
        wordSet1.setRepetitionClass(SEEN);

        WordSet wordSet2 = new WordSet(wordSet1);
        wordSet2.setId(3);
        expectedSets.put(KEY, asList(wordSet1, wordSet2));
        serviceFactory.getWordSetService().saveWordSets(expectedSets.get(KEY));
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);

        // when first connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenReturn(response);
        List<WordSet> actualSets = serviceFactory.getWordSetService().getWordSets(null);
        Thread.sleep(1000);

        // then
        assertEquals(expectedSets.get(KEY), actualSets);


        Mockito.reset(requestExecutor);
        Mockito.reset(gitHubRestClient);

        // when second connection
        Mockito.when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        actualSets = serviceFactory.getWordSetService().getWordSets(null);

        // then
        assertEquals(expectedSets.get(KEY), actualSets);


    }

    @Test
    public void findAllWordSets_internetExistsNever() throws JsonProcessingException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        WordSet wordSet1 = new WordSet();
        wordSet1.setWords(new ArrayList<Word2Tokens>());
        wordSet1.setTopicId("topic");
        wordSet1.setRepetitionClass(SEEN);

        WordSet wordSet2 = new WordSet(wordSet1);
        wordSet2.setId(3);
        expectedSets.put(KEY, asList(wordSet1, wordSet2));
        serviceFactory.getWordSetService().saveWordSets(expectedSets.get(KEY));

        // when first connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        List<WordSet> actualSets = serviceFactory.getWordSetService().getWordSets(null);

        // then
        assertEquals(expectedSets.get(KEY).size(), actualSets.size());


        Mockito.reset(requestExecutor);
        Mockito.reset(gitHubRestClient);

        // when second connection
        Mockito.when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        actualSets = serviceFactory.getWordSetService().getWordSets(null);

        assertEquals(expectedSets.get(KEY).size(), actualSets.size());


    }

    @Test
    public void findAllWordSets_internetExistsOnlySecondTime() throws JsonProcessingException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        WordSet wordSet1 = new WordSet();
        wordSet1.setWords(new ArrayList<Word2Tokens>());
        wordSet1.setTopicId("topic");
        wordSet1.setRepetitionClass(SEEN);

        WordSet wordSet2 = new WordSet(wordSet1);
        wordSet2.setId(3);
        expectedSets.put(KEY, asList(wordSet1, wordSet2));
        serviceFactory.getWordSetService().saveWordSets(expectedSets.get(KEY));
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);
        List<WordSetMapping> wordSetMappings = getWordSetMappings();

        // when first connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        List<WordSet> actualSets = serviceFactory.getWordSetService().getWordSets(null);

        // then
        assertEquals(expectedSets.get(KEY).size(), actualSets.size());


        Mockito.reset(requestExecutor);
        Mockito.reset(gitHubRestClient);

        // when second connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenReturn(response);
        actualSets = serviceFactory.getDataServer().findAllWordSets();

        // then
        assertEquals(expectedSets.get(KEY).size(), actualSets.size());
        Mockito.verify(gitHubRestClient).findAllWordSets();
        Mockito.verify(requestExecutor).execute(mockCall);
    }

    @NonNull
    private List<WordSetMapping> getWordSetMappings() throws JsonProcessingException {
        WordSetMapping wordSetMapping1 = new WordSetMapping();
        wordSetMapping1.setId("1");
        wordSetMapping1.setWords(mapper.writeValueAsString(emptyList()));
        WordSetMapping wordSetMapping2 = new WordSetMapping();
        wordSetMapping2.setId("2");
        wordSetMapping2.setWords(mapper.writeValueAsString(emptyList()));
        return asList(wordSetMapping1, wordSetMapping2);
    }
}