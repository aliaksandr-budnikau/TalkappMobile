package talkapp.org.talkappmobile.component.backend.impl;

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
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.AccountRestClient;
import talkapp.org.talkappmobile.component.backend.GitHubRestClient;
import talkapp.org.talkappmobile.component.backend.LoginRestClient;
import talkapp.org.talkappmobile.component.backend.SentenceRestClient;
import talkapp.org.talkappmobile.component.backend.TextGrammarCheckRestClient;
import talkapp.org.talkappmobile.component.backend.TopicRestClient;
import talkapp.org.talkappmobile.component.backend.WordSetRestClient;
import talkapp.org.talkappmobile.component.backend.WordTranslationRestClient;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordTranslationDao;
import talkapp.org.talkappmobile.component.database.dao.impl.local.WordSetDaoImpl;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.WordSet;

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

    @Mock
    private WordSetDaoImpl wordSetDaoMock;
    @Mock
    private TopicDao topicDao;
    @Mock
    private SentenceDao sentenceDao;
    @Mock
    private WordTranslationDao wordTranslationDao;
    @Mock
    private AuthSign authSign;
    @Mock
    private AccountRestClient accountRestClient;
    @Mock
    private LoginRestClient loginRestClient;
    @Mock
    private SentenceRestClient sentenceRestClient;
    @Mock
    private GitHubRestClient gitHubRestClient;
    @Mock
    private TextGrammarCheckRestClient textGrammarCheckRestClient;
    @Mock
    private TopicRestClient topicRestClient;
    @Mock
    private WordSetRestClient wordSetRestClient;
    @Mock
    private WordTranslationRestClient wordTranslationRestClient;
    @Mock
    private Logger logger;
    @Mock
    private RequestExecutor requestExecutor;
    private LocalDataService localDataService;
    private DataServerImpl server;
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
        server = new DataServerImpl(logger, authSign, accountRestClient,
                loginRestClient, sentenceRestClient, gitHubRestClient,
                textGrammarCheckRestClient, topicRestClient, wordSetRestClient,
                wordTranslationRestClient, localDataService, requestExecutor);
    }

    @Test
    public void findAllWordSets_internetExistsEachTime() throws InterruptedException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        List<WordSet> expectedSets = asList(new WordSet(), new WordSet());
        Response<List<WordSet>> response = Response.success(expectedSets);

        // when first connection
        when(wordSetRestClient.findAll(authSign)).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenReturn(response);
        List<WordSet> actualSets = server.findAllWordSets();
        Thread.sleep(1000);

        // then
        assertEquals(expectedSets, actualSets);
        verify(wordSetRestClient).findAll(authSign);
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(0)).queryForAll();
        verify(wordSetDaoMock, times(2)).createOrUpdate(any(WordSetMapping.class));

        reset(wordSetDaoMock);
        reset(wordSetRestClient);
        reset(requestExecutor);

        // when second connection
        when(wordSetRestClient.findAll(authSign)).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenReturn(response);
        actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets, actualSets);
        verify(wordSetRestClient).findAll(authSign);
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(0)).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));
    }

    @Test
    public void findAllWordSets_internetExistsFirstTimeOnly() throws InterruptedException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        List<WordSet> expectedSets = asList(new WordSet(), new WordSet());
        Response<List<WordSet>> response = Response.success(expectedSets);

        // when first connection
        when(wordSetRestClient.findAll(authSign)).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenReturn(response);
        List<WordSet> actualSets = server.findAllWordSets();
        Thread.sleep(1000);

        // then
        assertEquals(expectedSets, actualSets);
        verify(wordSetRestClient).findAll(authSign);
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(0)).queryForAll();
        verify(wordSetDaoMock, times(2)).createOrUpdate(any(WordSetMapping.class));

        reset(wordSetDaoMock);
        reset(wordSetRestClient);
        reset(requestExecutor);

        // when second connection
        when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        when(wordSetRestClient.findAll(authSign)).thenReturn(mockCall);
        actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets, actualSets);
        verify(wordSetRestClient).findAll(authSign);
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(0)).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));
    }

    @Test
    public void findAllWordSets_internetExistsNever() throws JsonProcessingException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        List<WordSet> expectedSets = asList(new WordSet(), new WordSet());
        List<WordSetMapping> wordSetMappings = getWordSetMappings();

        // when first connection
        when(wordSetRestClient.findAll(authSign)).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        when(wordSetDaoMock.queryForAll()).thenReturn(wordSetMappings);
        List<WordSet> actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.size(), actualSets.size());
        verify(wordSetRestClient).findAll(authSign);
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));

        reset(wordSetDaoMock);
        reset(wordSetRestClient);
        reset(requestExecutor);

        // when second connection
        when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        when(wordSetRestClient.findAll(authSign)).thenReturn(mockCall);
        actualSets = server.findAllWordSets();

        assertEquals(expectedSets.size(), actualSets.size());
        verify(wordSetRestClient).findAll(authSign);
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock, times(0)).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));
    }

    @Test
    public void findAllWordSets_internetExistsOnlySecondTime() throws JsonProcessingException, SQLException {
        // setup
        Call mockCall = mock(Call.class);
        List<WordSet> expectedSets = asList(new WordSet(), new WordSet());
        Response<List<WordSet>> response = Response.success(expectedSets);
        List<WordSetMapping> wordSetMappings = getWordSetMappings();

        // when first connection
        when(wordSetRestClient.findAll(authSign)).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        when(wordSetDaoMock.queryForAll()).thenReturn(wordSetMappings);
        List<WordSet> actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.size(), actualSets.size());
        verify(wordSetRestClient).findAll(authSign);
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDaoMock).queryForAll();
        verify(wordSetDaoMock, times(0)).createOrUpdate(any(WordSetMapping.class));

        reset(wordSetDaoMock);
        reset(wordSetRestClient);
        reset(requestExecutor);

        // when second connection
        when(wordSetRestClient.findAll(authSign)).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenReturn(response);
        actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.size(), actualSets.size());
        verify(wordSetRestClient).findAll(authSign);
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