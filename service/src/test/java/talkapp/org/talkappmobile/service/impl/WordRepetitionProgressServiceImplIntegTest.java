package talkapp.org.talkappmobile.service.impl;

import android.content.Context;

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

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordRepetitionProgress;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;
import talkapp.org.talkappmobile.repository.RepositoryFactory;
import talkapp.org.talkappmobile.repository.RepositoryFactoryImpl;
import talkapp.org.talkappmobile.repository.SentenceRepositoryImpl;
import talkapp.org.talkappmobile.repository.WordRepetitionProgressRepository;
import talkapp.org.talkappmobile.repository.WordSetRepository;
import talkapp.org.talkappmobile.service.BuildConfig;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static talkapp.org.talkappmobile.model.RepetitionClass.LEARNED;
import static talkapp.org.talkappmobile.model.RepetitionClass.NEW;
import static talkapp.org.talkappmobile.model.RepetitionClass.REPEATED;
import static talkapp.org.talkappmobile.model.RepetitionClass.SEEN;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class WordRepetitionProgressServiceImplIntegTest {

    private WordRepetitionProgressServiceImpl service;
    private ObjectMapper mapper;
    private RepositoryFactory repositoryFactory;

    @Before
    public void setUp() throws Exception {
        repositoryFactory = new RepositoryFactoryImpl(Mockito.mock(Context.class)) {
            private DatabaseHelper helper;

            @Override
            protected DatabaseHelper databaseHelper() {
                if (helper != null) {
                    return helper;
                }
                helper = getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
                return helper;
            }

        };
        SentenceDao sentenceDao = Mockito.mock(SentenceDao.class);
        mapper = new ObjectMapper();
        WordSetRepository wordSetRepository = repositoryFactory.getWordSetRepository();
        SentenceRepositoryImpl sentenceRepository = new SentenceRepositoryImpl(sentenceDao, mapper);
        WordRepetitionProgressRepository progressRepository = repositoryFactory.getWordRepetitionProgressRepository();
        service = new WordRepetitionProgressServiceImpl(progressRepository, wordSetRepository, sentenceRepository);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        int sourceWordSetId = 3;
        Word2Tokens anniversary = new Word2Tokens("anniversary", "anniversary", sourceWordSetId);
        WordSet wordSet = new WordSet();
        wordSet.setTopicId("" + sourceWordSetId);
        wordSet.setId(sourceWordSetId);
        wordSet.setWords(asList(anniversary));
        wordSetRepository.createNewOrUpdate(wordSet);
        for (int c = 0; c < 12; c++) {
            for (int i = 2; i <= 13; i++) {
                WordRepetitionProgress progress = new WordRepetitionProgress();
                progress.setSentenceIds(asList("AWbgbq6hNEXFMlzHK5Ul"));
                progress.setStatus(WordSetProgressStatus.FINISHED.name());
                cal.add(Calendar.HOUR, -2 * 24 * i);
                progress.setUpdatedDate(cal.getTime());
                progress.setRepetitionCounter(c);
                progress.setWordSetId(sourceWordSetId);
                progressRepository.createNewOrUpdate(progress);
            }
        }
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
        ServiceFactoryBean.removeInstance();
    }

    @Test
    public void test() {
        List<WordSet> wordSets = service.findFinishedWordSetsSortByUpdatedDate(24 * 2);
        assertEquals(NEW, wordSets.get(0).getRepetitionClass());
        assertEquals(SEEN, wordSets.get(1).getRepetitionClass());
        assertEquals(SEEN, wordSets.get(2).getRepetitionClass());
        assertEquals(REPEATED, wordSets.get(3).getRepetitionClass());
        assertEquals(REPEATED, wordSets.get(4).getRepetitionClass());
        assertEquals(REPEATED, wordSets.get(5).getRepetitionClass());
        assertEquals(REPEATED, wordSets.get(6).getRepetitionClass());
        assertEquals(LEARNED, wordSets.get(7).getRepetitionClass());
        assertEquals(LEARNED, wordSets.get(8).getRepetitionClass());
        assertEquals(LEARNED, wordSets.get(9).getRepetitionClass());
        assertEquals(LEARNED, wordSets.get(10).getRepetitionClass());
        assertEquals(LEARNED, wordSets.get(11).getRepetitionClass());
    }
}