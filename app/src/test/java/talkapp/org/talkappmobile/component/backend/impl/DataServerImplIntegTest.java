package talkapp.org.talkappmobile.component.backend.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import talkapp.org.talkappmobile.activity.presenter.PresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.GitHubRestClient;
import talkapp.org.talkappmobile.component.backend.SentenceRestClient;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordTranslationDao;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.SentenceContentScore.CORRUPTED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

@RunWith(MockitoJUnitRunner.class)
public class DataServerImplIntegTest extends PresenterAndInteractorIntegTest {
    @Mock
    private TopicDao topicDao;
    @Mock
    private SentenceDao sentenceDao;
    @Mock
    private WordTranslationDao wordTranslationDao;
    @Mock
    private SentenceRestClient sentenceRestClient;
    @Mock
    private GitHubRestClient gitHubRestClient;
    @Mock
    private Logger logger;
    @Mock
    private RequestExecutor requestExecutor;
    private LocalDataService localDataService;
    private DataServerImpl server;
    private ObjectMapper mapper = new ObjectMapper();
    private WordSetDao wordSetDao;

    @Before
    public void init() throws SQLException {
        wordSetDao = provideWordSetDao();
        localDataService = new LocalDataServiceImpl(wordSetDao, topicDao, sentenceDao, wordTranslationDao, mapper, logger);
        server = new DataServerImpl(sentenceRestClient, gitHubRestClient, localDataService, requestExecutor);
    }

    @Test
    public void findAllWordSets_losingOfProgressForWordSets() throws InterruptedException {
        // setup
        Call mockCall = mock(Call.class);

        WordSetMapping wordSetMapping1 = new WordSetMapping();
        wordSetMapping1.setId("1");
        wordSetMapping1.setStatus(FINISHED);
        wordSetMapping1.setTrainingExperience(10);
        WordSetMapping wordSetMapping2 = new WordSetMapping();
        wordSetMapping2.setId("2");
        wordSetMapping2.setStatus(SECOND_CYCLE);
        wordSetMapping2.setTrainingExperience(11);
        List<WordSetMapping> wordSetsMappingsWithProgress = asList(wordSetMapping1, wordSetMapping2);
        wordSetDao.refreshAll(wordSetsMappingsWithProgress);

        WordSet wordSet1 = new WordSet();
        wordSet1.setId(1);
        wordSet1.setTrainingExperience(0);
        wordSet1.setStatus(FIRST_CYCLE);
        wordSet1.setWords(new LinkedList<Word2Tokens>());
        WordSet wordSet2 = new WordSet();
        wordSet2.setId(2);
        wordSet2.setTrainingExperience(0);
        wordSet2.setStatus(FIRST_CYCLE);
        wordSet2.setWords(new LinkedList<Word2Tokens>());
        List<WordSet> wordSets = asList(wordSet1, wordSet2);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        expectedSets.put(1, wordSets);
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);
        when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenReturn(response);

        List<WordSet> actualSets = server.findAllWordSets();
        Thread.sleep(1000);

        // then
        assertEquals(10, actualSets.get(0).getTrainingExperience());
        assertEquals(FINISHED, actualSets.get(0).getStatus());
        assertEquals(11, actualSets.get(1).getTrainingExperience());
        assertEquals(SECOND_CYCLE, actualSets.get(1).getStatus());
    }

    @Test
    public void findAllWordSets_savingCopiesInWordSet() throws InterruptedException {
        // setup
        Call mockCall = mock(Call.class);

        WordSetMapping wordSetMapping1 = new WordSetMapping();
        wordSetMapping1.setId("1");
        wordSetMapping1.setStatus(FINISHED);
        wordSetMapping1.setTrainingExperience(10);
        WordSetMapping wordSetMapping2 = new WordSetMapping();
        wordSetMapping2.setId("2");
        wordSetMapping2.setStatus(SECOND_CYCLE);
        wordSetMapping2.setTrainingExperience(11);
        List<WordSetMapping> wordSetsMappingsWithProgress = asList(wordSetMapping1, wordSetMapping2);
        wordSetDao.refreshAll(wordSetsMappingsWithProgress);

        WordSet wordSet1 = new WordSet();
        wordSet1.setId(1);
        wordSet1.setTrainingExperience(0);
        wordSet1.setStatus(FIRST_CYCLE);
        Word2Tokens word1 = new Word2Tokens();
        word1.setWord("age");
        Word2Tokens word2 = new Word2Tokens();
        word2.setWord("age");
        wordSet1.setWords(asList(word1, word2));
        WordSet wordSet2 = new WordSet();
        wordSet2.setId(2);
        wordSet2.setTrainingExperience(0);
        wordSet2.setStatus(FIRST_CYCLE);
        word1 = new Word2Tokens();
        word1.setWord("age");
        word2 = new Word2Tokens();
        word2.setWord("age");
        wordSet2.setWords(asList(word1, word2));
        List<WordSet> wordSets = asList(wordSet1, wordSet2);
        Map<Integer, List<WordSet>> expectedSets = new HashMap<>();
        expectedSets.put(1, wordSets);
        Response<Map<Integer, List<WordSet>>> response = Response.success(expectedSets);
        when(gitHubRestClient.findAllWordSets()).thenReturn(mockCall);
        when(requestExecutor.execute(mockCall)).thenReturn(response);

        List<WordSet> actualSets = server.findAllWordSets();
        Thread.sleep(1000);

        // then
        assertEquals(1, actualSets.get(0).getWords().size());
        assertEquals(1, actualSets.get(1).getWords().size());
    }

    @Test
    public void sendSentenceScore() {
        DataServer dataServer = getServer();
        Sentence sentence = new Sentence();
        sentence.setText("testText");
        sentence.setTokens(asList(new TextToken(), new TextToken()));
        sentence.setContentScore(CORRUPTED);
        sentence.setTranslations(new HashMap<String, String>());
        sentence.setId("testId#word#6");
        assertTrue(dataServer.saveSentenceScore(sentence));
    }
}