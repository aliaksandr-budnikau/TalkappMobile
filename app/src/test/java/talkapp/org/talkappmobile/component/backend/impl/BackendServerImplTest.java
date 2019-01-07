package talkapp.org.talkappmobile.component.backend.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.component.AuthSign;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.AccountRestClient;
import talkapp.org.talkappmobile.component.backend.LoginRestClient;
import talkapp.org.talkappmobile.component.backend.SentenceRestClient;
import talkapp.org.talkappmobile.component.backend.TextGrammarCheckRestClient;
import talkapp.org.talkappmobile.component.backend.TopicRestClient;
import talkapp.org.talkappmobile.component.backend.WordSetRestClient;
import talkapp.org.talkappmobile.component.backend.WordTranslationRestClient;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.WordSet;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BackendServerImplTest {

    @Mock
    private WordSetDao wordSetDao;
    @Mock
    private TopicDao topicDao;
    @Mock
    private SentenceDao sentenceDao;
    @Mock
    private AuthSign authSign;
    @Mock
    private AccountRestClient accountRestClient;
    @Mock
    private LoginRestClient loginRestClient;
    @Mock
    private SentenceRestClient sentenceRestClient;
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
    private BackendServerImpl server;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init() {
        localDataService = new LocalDataServiceImpl(wordSetDao, topicDao, sentenceDao, mapper, logger);
        server = new BackendServerImpl(logger, authSign, accountRestClient,
                loginRestClient, sentenceRestClient,
                textGrammarCheckRestClient, topicRestClient, wordSetRestClient,
                wordTranslationRestClient, localDataService, requestExecutor);
    }

    @Test
    public void findAllWordSets_internetExistsEachTime() throws InterruptedException {
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
        verify(wordSetDao, times(0)).findAll();
        verify(wordSetDao).save(ArgumentMatchers.<WordSetMapping>anyList());

        reset(wordSetDao);
        reset(wordSetRestClient);
        reset(requestExecutor);

        // when second connection
        actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets, actualSets);
        verify(wordSetRestClient, times(0)).findAll(authSign);
        verify(requestExecutor, times(0)).execute(mockCall);
        verify(wordSetDao, times(0)).findAll();
        verify(wordSetDao, times(0)).save(ArgumentMatchers.<WordSetMapping>anyList());
    }

    @Test
    public void findAllWordSets_internetExistsFirstTimeOnly() throws InterruptedException {
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
        verify(wordSetDao, times(0)).findAll();
        verify(wordSetDao).save(ArgumentMatchers.<WordSetMapping>anyList());

        reset(wordSetDao);
        reset(wordSetRestClient);
        reset(requestExecutor);

        // when second connection
        actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets, actualSets);
        verify(wordSetRestClient, times(0)).findAll(authSign);
        verify(requestExecutor, times(0)).execute(mockCall);
        verify(wordSetDao, times(0)).findAll();
        verify(wordSetDao, times(0)).save(ArgumentMatchers.<WordSetMapping>anyList());
    }

    @Test
    public void findAllWordSets_internetExistsNever() throws JsonProcessingException {
        // setup
        Call mockCall = mock(Call.class);
        List<WordSet> expectedSets = asList(new WordSet(), new WordSet());
        List<WordSetMapping> wordSetMappings = getWordSetMappings();

        // when first connection
        when(wordSetRestClient.findAll(authSign)).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        when(wordSetDao.findAll()).thenReturn(wordSetMappings);
        List<WordSet> actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.size(), actualSets.size());
        verify(wordSetRestClient).findAll(authSign);
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDao).findAll();
        verify(wordSetDao, times(0)).save(ArgumentMatchers.<WordSetMapping>anyList());

        reset(wordSetDao);
        reset(wordSetRestClient);
        reset(requestExecutor);

        // when second connection
        actualSets = server.findAllWordSets();

        assertEquals(expectedSets.size(), actualSets.size());
        verify(wordSetRestClient, times(0)).findAll(authSign);
        verify(requestExecutor, times(0)).execute(mockCall);
        verify(wordSetDao, times(0)).findAll();
        verify(wordSetDao, times(0)).save(ArgumentMatchers.<WordSetMapping>anyList());
    }

    @Test
    public void findAllWordSets_internetExistsOnlySecondTime() throws JsonProcessingException {
        // setup
        Call mockCall = mock(Call.class);
        List<WordSet> expectedSets = asList(new WordSet(), new WordSet());
        List<WordSetMapping> wordSetMappings = getWordSetMappings();

        // when first connection
        when(wordSetRestClient.findAll(authSign)).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenThrow(InternetConnectionLostException.class);
        when(wordSetDao.findAll()).thenReturn(wordSetMappings);
        List<WordSet> actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.size(), actualSets.size());
        verify(wordSetRestClient).findAll(authSign);
        verify(requestExecutor).execute(mockCall);
        verify(wordSetDao).findAll();
        verify(wordSetDao, times(0)).save(ArgumentMatchers.<WordSetMapping>anyList());

        reset(wordSetDao);
        reset(wordSetRestClient);
        reset(requestExecutor);

        // when second connection
        actualSets = server.findAllWordSets();

        // then
        assertEquals(expectedSets.size(), actualSets.size());
        verify(wordSetRestClient, times(0)).findAll(authSign);
        verify(requestExecutor, times(0)).execute(mockCall);
        verify(wordSetDao, times(0)).findAll();
        verify(wordSetDao, times(0)).save(ArgumentMatchers.<WordSetMapping>anyList());
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