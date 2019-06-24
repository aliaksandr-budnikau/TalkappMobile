package talkapp.org.talkappmobile.component.backend.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.talkappmobile.DatabaseHelper;
import org.talkappmobile.dao.SentenceDao;
import org.talkappmobile.dao.TopicDao;
import org.talkappmobile.dao.WordSetDao;
import org.talkappmobile.dao.WordTranslationDao;
import org.talkappmobile.dao.impl.WordSetDaoImpl;
import org.talkappmobile.mappings.WordSetMapping;
import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.TextToken;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.presenter.PresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.GitHubRestClient;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.impl.LoggerBean;

import static android.os.Build.VERSION_CODES.M;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.talkappmobile.model.SentenceContentScore.CORRUPTED;
import static org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {M}, packageName = "org.talkappmobile.dao.impl")
public class DataServerImplIntegTest extends PresenterAndInteractorIntegTest {
    private TopicDao topicDao;
    private SentenceDao sentenceDao;
    private WordTranslationDao wordTranslationDao;
    private GitHubRestClient gitHubRestClient;
    private Logger logger;
    private RequestExecutor requestExecutor;
    private LocalDataService localDataService;
    private DataServer server;
    private ObjectMapper mapper = new ObjectMapper();
    private WordSetDao wordSetDao;

    @Before
    public void init() throws SQLException {
        topicDao = mock(TopicDao.class);
        sentenceDao = mock(SentenceDao.class);
        wordTranslationDao = mock(WordTranslationDao.class);
        gitHubRestClient = mock(GitHubRestClient.class);
        requestExecutor = mock(RequestExecutor.class);
        logger = mock(Logger.class);

        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        wordSetDao = new WordSetDaoImpl(databaseHelper.getConnectionSource(), WordSetMapping.class);
        localDataService = new LocalDataServiceImpl(wordSetDao, topicDao, sentenceDao, wordTranslationDao, mapper, logger);

        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(localDataService);
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        server = factory.get();
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void findAllWordSets_losingOfProgressForWordSets() throws InterruptedException {
        // setup
        Call mockCall = mock(Call.class);

        WordSetMapping wordSetMapping1 = new WordSetMapping();
        wordSetMapping1.setId("1");
        wordSetMapping1.setStatus(FINISHED.name());
        wordSetMapping1.setTrainingExperience(10);
        wordSetMapping1.setTopicId("22");
        wordSetMapping1.setWords("[{\"word\":\"22\", \"tokens\":\"22\"}]");
        WordSetMapping wordSetMapping2 = new WordSetMapping();
        wordSetMapping2.setId("2");
        wordSetMapping2.setStatus(SECOND_CYCLE.name());
        wordSetMapping2.setTrainingExperience(11);
        wordSetMapping2.setTopicId("22");
        wordSetMapping2.setWords("[{\"word\":\"22\", \"tokens\":\"22\"}]");
        List<WordSetMapping> wordSetsMappingsWithProgress = asList(wordSetMapping1, wordSetMapping2);
        wordSetDao.refreshAll(wordSetsMappingsWithProgress);

        WordSet wordSet1 = new WordSet();
        wordSet1.setId(1);
        wordSet1.setTrainingExperience(0);
        wordSet1.setStatus(FIRST_CYCLE);
        wordSet1.setWords(new LinkedList<Word2Tokens>());
        wordSet1.setTopicId("22");
        wordSet1.setWords(singletonList(new Word2Tokens("", "", 1)));
        WordSet wordSet2 = new WordSet();
        wordSet2.setId(2);
        wordSet2.setTrainingExperience(0);
        wordSet2.setStatus(FIRST_CYCLE);
        wordSet2.setWords(new LinkedList<Word2Tokens>());
        wordSet2.setTopicId("22");
        wordSet2.setWords(singletonList(new Word2Tokens("", "", 1)));
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
        wordSetMapping1.setStatus(FINISHED.name());
        wordSetMapping1.setTrainingExperience(10);
        wordSetMapping1.setTopicId("22");
        wordSetMapping1.setWords("[{\"word\":\"22\", \"tokens\":\"22\"}]");
        WordSetMapping wordSetMapping2 = new WordSetMapping();
        wordSetMapping2.setId("2");
        wordSetMapping2.setStatus(SECOND_CYCLE.name());
        wordSetMapping2.setTrainingExperience(11);
        wordSetMapping2.setTopicId("22");
        wordSetMapping2.setWords("[{\"word\":\"22\", \"tokens\":\"22\"}]");
        List<WordSetMapping> wordSetsMappingsWithProgress = asList(wordSetMapping1, wordSetMapping2);
        wordSetDao.refreshAll(wordSetsMappingsWithProgress);

        WordSet wordSet1 = new WordSet();
        wordSet1.setId(1);
        wordSet1.setTrainingExperience(0);
        wordSet1.setStatus(FIRST_CYCLE);
        Word2Tokens word1 = new Word2Tokens("age", "age", wordSet1.getId());
        Word2Tokens word2 = new Word2Tokens("age", "age", wordSet1.getId());
        wordSet1.setWords(asList(word1, word2));
        WordSet wordSet2 = new WordSet();
        wordSet2.setId(2);
        wordSet2.setTrainingExperience(0);
        wordSet2.setStatus(FIRST_CYCLE);
        word1 = new Word2Tokens("age", "age", wordSet2.getId());
        word2 = new Word2Tokens("age", "age", wordSet2.getId());
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
        Sentence sentence = new Sentence();
        sentence.setText("testText");
        sentence.setTokens(asList(new TextToken(), new TextToken()));
        sentence.setContentScore(CORRUPTED);
        sentence.setTranslations(new HashMap<String, String>());
        sentence.setId("testId#word#6");
        //assertTrue(server.saveSentenceScore(sentence));
    }
}