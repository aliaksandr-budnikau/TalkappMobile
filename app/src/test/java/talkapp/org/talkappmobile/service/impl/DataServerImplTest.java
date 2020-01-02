package talkapp.org.talkappmobile.service.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.AndroidConnectionSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.TopicDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.dao.impl.WordSetDaoImpl;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.CachedDataServerDecorator;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.GitHubRestClient;
import talkapp.org.talkappmobile.service.LocalDataService;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.SentenceRestClient;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataServerImplTest {

    public static final int KEY = 1;
    @Mock
    private WordSetDaoImpl wordSetDaoMock;
    @Mock
    private TopicDao topicDao;
    @Mock
    private SentenceDao sentenceDao;
    @Mock
    private WordTranslationDao wordTranslationDao;
    @Mock
    private ExpAuditDao expAuditDao;
    @Mock
    private SentenceRestClient sentenceRestClient;
    @Mock
    private GitHubRestClient gitHubRestClient;
    @Mock
    private Logger logger;
    @Mock
    private RequestExecutor requestExecutor;
    @Mock
    private WordTranslationService wordTranslationService;
    private LocalDataService localDataService;
    private DataServer server;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init() throws SQLException {
        AndroidConnectionSource source = new AndroidConnectionSource(new DatabaseHelper(mock(Context.class)));
        WordSetDaoImpl wordSetDao = new WordSetDaoImpl(source, WordSetMapping.class) {
            @Override
            public List<WordSetMapping> queryForAll() throws SQLException {
                return wordSetDaoMock.queryForAll();
            }

            @Override
            public synchronized CreateOrUpdateStatus createOrUpdate(WordSetMapping data) throws SQLException {
                return wordSetDaoMock.createOrUpdate(data);
            }
        };
        localDataService = new LocalDataServiceImpl(wordSetDao, topicDao, sentenceDao, wordTranslationDao, mapper, logger);
        server = new CachedDataServerDecorator(new DataServerImpl(sentenceRestClient, gitHubRestClient, requestExecutor), localDataService, wordTranslationService);
    }

    @Test
    public void findAllWordSets_internetExistsEachTime() throws InterruptedException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        WordSet wordSet1 = new WordSet();
        wordSet1.setWords(new LinkedList<Word2Tokens>());
        WordSet wordSet2 = new WordSet();
        wordSet2.setWords(new LinkedList<Word2Tokens>());
        expectedSets.put(1, asList(wordSet1, wordSet2));
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);

        // when first connection
        when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenReturn(response);
        List<WordSet> actualSets = server.findAllWordSets();
        Thread.sleep(1000);

        // then
        assertEquals(expectedSets.get(KEY), actualSets);
        verify(gitHubRestClient).findAllWordSets();
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(2)).queryForAll();
        verify(wordSetDaoMock, times(2)).createOrUpdate(any(WordSetMapping.class));

        reset(wordSetDaoMock);
        reset(requestExecutor);
        reset(gitHubRestClient);

        // when second connection
        when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenReturn(response);
        actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.get(KEY), actualSets);
        verify(gitHubRestClient).findAllWordSets();
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(0)).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));
    }

    @Test
    public void findAllWordSets_internetExistsFirstTimeOnly() throws InterruptedException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        WordSet wordSet1 = new WordSet();
        wordSet1.setWords(new LinkedList<Word2Tokens>());
        WordSet wordSet2 = new WordSet();
        wordSet2.setWords(new LinkedList<Word2Tokens>());
        expectedSets.put(1, asList(wordSet1, wordSet2));
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);

        // when first connection
        when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenReturn(response);
        List<WordSet> actualSets = server.findAllWordSets();
        Thread.sleep(1000);

        // then
        assertEquals(expectedSets.get(KEY), actualSets);
        verify(gitHubRestClient).findAllWordSets();
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(2)).queryForAll();
        verify(wordSetDaoMock, times(2)).createOrUpdate(any(WordSetMapping.class));

        reset(wordSetDaoMock);
        reset(requestExecutor);
        reset(gitHubRestClient);

        // when second connection
        when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.get(KEY), actualSets);
        verify(gitHubRestClient).findAllWordSets();
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(0)).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));
    }

    @Test
    public void findAllWordSets_internetExistsNever() throws JsonProcessingException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        expectedSets.put(KEY, asList(new WordSet(), new WordSet()));
        List<WordSetMapping> wordSetMappings = getWordSetMappings();

        // when first connection
        when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        when(wordSetDaoMock.queryForAll()).thenReturn(wordSetMappings);
        List<WordSet> actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.get(KEY).size(), actualSets.size());
        verify(gitHubRestClient).findAllWordSets();
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));

        reset(wordSetDaoMock);
        reset(requestExecutor);
        reset(gitHubRestClient);

        // when second connection
        when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        actualSets = server.findAllWordSets();

        assertEquals(expectedSets.get(KEY).size(), actualSets.size());
        verify(gitHubRestClient).findAllWordSets();
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(0)).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));
    }

    @Test
    public void findAllWordSets_internetExistsOnlySecondTime() throws JsonProcessingException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        WordSet wordSet1 = new WordSet();
        wordSet1.setWords(new LinkedList<Word2Tokens>());
        WordSet wordSet2 = new WordSet();
        wordSet2.setWords(new LinkedList<Word2Tokens>());
        expectedSets.put(1, asList(wordSet1, wordSet2));
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);
        List<WordSetMapping> wordSetMappings = getWordSetMappings();

        // when first connection
        when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        when(wordSetDaoMock.queryForAll()).thenReturn(wordSetMappings);
        List<WordSet> actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.get(KEY).size(), actualSets.size());
        verify(gitHubRestClient).findAllWordSets();
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));

        reset(wordSetDaoMock);
        reset(requestExecutor);
        reset(gitHubRestClient);

        // when second connection
        when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenReturn(response);
        actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.get(KEY).size(), actualSets.size());
        verify(gitHubRestClient).findAllWordSets();
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(0)).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));
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