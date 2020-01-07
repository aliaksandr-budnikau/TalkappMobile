package talkapp.org.talkappmobile.service.impl;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.TextToken;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.GitHubRestClient;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

import static android.os.Build.VERSION_CODES.M;
import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static talkapp.org.talkappmobile.model.SentenceContentScore.CORRUPTED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {M}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class DataServerImplIntegTest {
    private GitHubRestClient gitHubRestClient;
    private RequestExecutor requestExecutor;
    private WordSetService wordSetService;
    private ServiceFactoryBean serviceFactory;
    private WordSetMapper wordSetMapper;

    @Before
    public void init() {
        gitHubRestClient = mock(GitHubRestClient.class);
        requestExecutor = mock(RequestExecutor.class);

        serviceFactory = new ServiceFactoryBean() {
            private DatabaseHelper helper;

            @Override
            protected DatabaseHelper databaseHelper() {
                if (helper != null) {
                    return helper;
                }
                helper = getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
                return helper;
            }

            @Override
            protected GitHubRestClient gitHubRestClient() {
                return gitHubRestClient;
            }

            @Override
            public RequestExecutor getRequestExecutor() {
                return requestExecutor;
            }
        };
        serviceFactory.setContext(mock(Context.class));
        wordSetMapper = new WordSetMapper(new ObjectMapper());
        wordSetService = serviceFactory.getWordSetExperienceRepository();
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
        List<WordSet> wordSetsMappingsWithProgress = asList(wordSetMapper.toDto(wordSetMapping1), wordSetMapper.toDto(wordSetMapping2));
        serviceFactory.getWordSetExperienceRepository().saveWordSets(wordSetsMappingsWithProgress);

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

        List<WordSet> actualSets = wordSetService.getWordSets(null);
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
        List<WordSet> wordSetsMappingsWithProgress = asList(wordSetMapper.toDto(wordSetMapping1), wordSetMapper.toDto(wordSetMapping2));
        serviceFactory.getWordSetExperienceRepository().saveWordSets(wordSetsMappingsWithProgress);

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

        List<WordSet> actualSets = wordSetService.getWordSets(null);
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