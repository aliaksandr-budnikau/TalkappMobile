package talkapp.org.talkappmobile.component.database.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.activity.presenter.PresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.impl.WordRepetitionProgressDaoImpl;
import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Calendar.getInstance;
import static okhttp3.internal.Util.UTC;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static talkapp.org.talkappmobile.model.RepetitionClass.LEARNED;
import static talkapp.org.talkappmobile.model.RepetitionClass.NEW;
import static talkapp.org.talkappmobile.model.RepetitionClass.REPEATED;
import static talkapp.org.talkappmobile.model.RepetitionClass.SEEN;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.component.database.dao.impl")
public class WordRepetitionProgressServiceImplIntegTest extends PresenterAndInteractorIntegTest {

    private WordRepetitionProgressServiceImpl service;
    private WordRepetitionProgressDao exerciseDao;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {

        DatabaseHelper databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        SentenceDao sentenceDao = mock(SentenceDao.class);
        WordSetDao wordSetDao = mock(WordSetDao.class);
        exerciseDao = new WordRepetitionProgressDaoImpl(databaseHelper.getConnectionSource(), WordRepetitionProgressMapping.class);
        mapper = new ObjectMapper();
        service = new WordRepetitionProgressServiceImpl(exerciseDao, wordSetDao, sentenceDao, mapper);

        Calendar cal = getInstance(UTC);

        Word2Tokens anniversary = new Word2Tokens("anniversary");
        anniversary.setTokens("anniversary");

        for (int c = 0; c < 12; c++) {
            for (int i = 2; i <= 13; i++) {
                WordRepetitionProgressMapping exercise = new WordRepetitionProgressMapping();
                exercise.setSentenceId("AWbgbq6hNEXFMlzHK5Ul#" + anniversary.getWord() + "#6");
                exercise.setStatus(WordSetProgressStatus.FINISHED);
                cal.add(Calendar.HOUR, -2 * 24 * i);
                exercise.setUpdatedDate(cal.getTime());
                exercise.setRepetitionCounter(c);
                exercise.setWordJSON(mapper.writeValueAsString(anniversary));
                exerciseDao.createNewOrUpdate(exercise);
            }
        }
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
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