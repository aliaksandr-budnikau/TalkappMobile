package talkapp.org.talkappmobile.service.impl;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.dao.impl.WordSetDaoImpl;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.repository.TopicRepository;
import talkapp.org.talkappmobile.repository.WordSetRepositoryImpl;
import talkapp.org.talkappmobile.service.CachedWordSetServiceDecorator;
import talkapp.org.talkappmobile.service.DataServer;
import talkapp.org.talkappmobile.service.DataServerImpl;
import talkapp.org.talkappmobile.service.GitHubRestClient;
import talkapp.org.talkappmobile.service.InternetConnectionLostException;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RequestExecutor;
import talkapp.org.talkappmobile.service.SentenceRestClient;
import talkapp.org.talkappmobile.service.TopicService;
import talkapp.org.talkappmobile.service.TopicServiceImpl;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordSetServiceImpl;
import talkapp.org.talkappmobile.service.WordTranslationService;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class DataServerImplTest {

    public static final int KEY = 1;
    @Mock
    private WordSetDaoImpl wordSetDaoMock;
    @Mock
    private TopicRepository topicRepository;
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
    private TopicService topicService;
    private DataServer server;
    private ObjectMapper mapper = new ObjectMapper();
    private WordSetService wordSetService;

    @Before
    public void init() throws SQLException {
        AndroidConnectionSource source = new AndroidConnectionSource(Mockito.mock(DatabaseHelper.class));
        WordSetDaoImpl wordSetDao = new WordSetDaoImpl(source, WordSetMapping.class) {
            @Override
            public List<WordSetMapping> queryForAll() throws SQLException {
                return wordSetDaoMock.queryForAll();
            }

            @Override
            public synchronized Dao.CreateOrUpdateStatus createOrUpdate(WordSetMapping data) throws SQLException {
                return wordSetDaoMock.createOrUpdate(data);
            }
        };
        topicService = new TopicServiceImpl(topicRepository, server);
        server = new DataServerImpl(sentenceRestClient, gitHubRestClient, requestExecutor);
        WordSetRepositoryImpl wordSetRepository = new WordSetRepositoryImpl(wordSetDao, Mockito.mock(NewWordSetDraftDao.class), mapper);
        WordSetServiceImpl wordSetService = new WordSetServiceImpl(server, wordSetRepository);
        this.wordSetService = new CachedWordSetServiceDecorator(wordSetRepository, wordSetService);
    }

    @Test
    public void findAllWordSets_internetExistsEachTime() throws InterruptedException, SQLException {
        // setup
        Call mockCall = Mockito.mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        WordSet wordSet1 = new WordSet();
        wordSet1.setWords(new LinkedList<Word2Tokens>());
        WordSet wordSet2 = new WordSet();
        wordSet2.setWords(new LinkedList<Word2Tokens>());
        expectedSets.put(1, asList(wordSet1, wordSet2));
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);

        // when first connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenReturn(response);
        List<WordSet> actualSets = wordSetService.getWordSets(null);
        Thread.sleep(1000);

        // then
        assertEquals(expectedSets.get(KEY), actualSets);
        Mockito.verify(gitHubRestClient).findAllWordSets();
        Mockito.verify(requestExecutor).execute(mockCall);
        Mockito.verify(wordSetDaoMock, Mockito.times(2)).queryForAll();
        Mockito.verify(wordSetDaoMock, Mockito.times(2)).createOrUpdate(ArgumentMatchers.any(WordSetMapping.class));

        Mockito.reset(wordSetDaoMock);
        Mockito.reset(requestExecutor);
        Mockito.reset(gitHubRestClient);

        // when second connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenReturn(response);
        actualSets = wordSetService.getWordSets(null);

        // then
        assertEquals(expectedSets.get(KEY), actualSets);
        Mockito.verify(gitHubRestClient).findAllWordSets();
        Mockito.verify(requestExecutor).execute(mockCall);
        Mockito.verify(wordSetDaoMock, Mockito.times(0)).queryForAll();
        Mockito.verify(wordSetDaoMock, Mockito.times(0)).createOrUpdate(ArgumentMatchers.any(WordSetMapping.class));
    }

    @Test
    public void findAllWordSets_internetExistsFirstTimeOnly() throws InterruptedException, SQLException {
        // setup
        Call mockCall = Mockito.mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        WordSet wordSet1 = new WordSet();
        wordSet1.setWords(new LinkedList<Word2Tokens>());
        WordSet wordSet2 = new WordSet();
        wordSet2.setWords(new LinkedList<Word2Tokens>());
        expectedSets.put(1, asList(wordSet1, wordSet2));
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);

        // when first connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenReturn(response);
        List<WordSet> actualSets = wordSetService.getWordSets(null);
        Thread.sleep(1000);

        // then
        assertEquals(expectedSets.get(KEY), actualSets);
        Mockito.verify(gitHubRestClient).findAllWordSets();
        Mockito.verify(requestExecutor).execute(mockCall);
        Mockito.verify(wordSetDaoMock, Mockito.times(2)).queryForAll();
        Mockito.verify(wordSetDaoMock, Mockito.times(2)).createOrUpdate(ArgumentMatchers.any(WordSetMapping.class));

        Mockito.reset(wordSetDaoMock);
        Mockito.reset(requestExecutor);
        Mockito.reset(gitHubRestClient);

        // when second connection
        Mockito.when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        actualSets = wordSetService.getWordSets(null);

        // then
        assertEquals(expectedSets.get(KEY), actualSets);
        Mockito.verify(gitHubRestClient).findAllWordSets();
        Mockito.verify(requestExecutor).execute(mockCall);
        Mockito.verify(wordSetDaoMock, Mockito.times(0)).queryForAll();
        Mockito.verify(wordSetDaoMock, Mockito.times(0)).createOrUpdate(ArgumentMatchers.any(WordSetMapping.class));
    }

    @Test
    public void findAllWordSets_internetExistsNever() throws JsonProcessingException, SQLException {
        // setup
        Call mockCall = Mockito.mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        expectedSets.put(KEY, asList(new WordSet(), new WordSet()));
        List<WordSetMapping> wordSetMappings = getWordSetMappings();

        // when first connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        Mockito.when(wordSetDaoMock.queryForAll()).thenReturn(wordSetMappings);
        List<WordSet> actualSets = wordSetService.getWordSets(null);

        // then
        assertEquals(expectedSets.get(KEY).size(), actualSets.size());
        Mockito.verify(gitHubRestClient).findAllWordSets();
        Mockito.verify(requestExecutor).execute(mockCall);
        Mockito.verify(wordSetDaoMock).queryForAll();
        Mockito.verify(wordSetDaoMock, Mockito.times(0)).createOrUpdate(ArgumentMatchers.any(WordSetMapping.class));

        Mockito.reset(wordSetDaoMock);
        Mockito.reset(requestExecutor);
        Mockito.reset(gitHubRestClient);

        // when second connection
        Mockito.when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        actualSets = wordSetService.getWordSets(null);

        assertEquals(expectedSets.get(KEY).size(), actualSets.size());
        Mockito.verify(gitHubRestClient).findAllWordSets();
        Mockito.verify(requestExecutor).execute(mockCall);
        Mockito.verify(wordSetDaoMock, Mockito.times(0)).queryForAll();
        Mockito.verify(wordSetDaoMock, Mockito.times(0)).createOrUpdate(ArgumentMatchers.any(WordSetMapping.class));
    }

    @Test
    public void findAllWordSets_internetExistsOnlySecondTime() throws JsonProcessingException, SQLException {
        // setup
        Call mockCall = Mockito.mock(Call.class);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        WordSet wordSet1 = new WordSet();
        wordSet1.setWords(new LinkedList<Word2Tokens>());
        WordSet wordSet2 = new WordSet();
        wordSet2.setWords(new LinkedList<Word2Tokens>());
        expectedSets.put(1, asList(wordSet1, wordSet2));
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);
        List<WordSetMapping> wordSetMappings = getWordSetMappings();

        // when first connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        Mockito.when(wordSetDaoMock.queryForAll()).thenReturn(wordSetMappings);
        List<WordSet> actualSets = wordSetService.getWordSets(null);

        // then
        assertEquals(expectedSets.get(KEY).size(), actualSets.size());
        Mockito.verify(gitHubRestClient).findAllWordSets();
        Mockito.verify(requestExecutor).execute(mockCall);
        Mockito.verify(wordSetDaoMock).queryForAll();
        Mockito.verify(wordSetDaoMock, Mockito.times(0)).createOrUpdate(ArgumentMatchers.any(WordSetMapping.class));

        Mockito.reset(wordSetDaoMock);
        Mockito.reset(requestExecutor);
        Mockito.reset(gitHubRestClient);

        // when second connection
        Mockito.when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        Mockito.when(requestExecutor.execute(mockCall)).thenReturn(response);
        actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.get(KEY).size(), actualSets.size());
        Mockito.verify(gitHubRestClient).findAllWordSets();
        Mockito.verify(requestExecutor).execute(mockCall);
        Mockito.verify(wordSetDaoMock, Mockito.times(0)).queryForAll();
        Mockito.verify(wordSetDaoMock, Mockito.times(0)).createOrUpdate(ArgumentMatchers.any(WordSetMapping.class));
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