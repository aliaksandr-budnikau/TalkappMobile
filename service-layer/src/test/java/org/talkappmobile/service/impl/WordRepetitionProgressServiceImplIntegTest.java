package org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.talkappmobile.DatabaseHelper;
import org.talkappmobile.dao.SentenceDao;
import org.talkappmobile.dao.WordRepetitionProgressDao;
import org.talkappmobile.dao.WordSetDao;
import org.talkappmobile.dao.impl.WordRepetitionProgressDaoImpl;
import org.talkappmobile.mappings.WordRepetitionProgressMapping;
import org.talkappmobile.model.Word2Tokens;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.model.WordSetProgressStatus;
import org.talkappmobile.service.BuildConfig;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.talkappmobile.model.RepetitionClass.LEARNED;
import static org.talkappmobile.model.RepetitionClass.NEW;
import static org.talkappmobile.model.RepetitionClass.REPEATED;
import static org.talkappmobile.model.RepetitionClass.SEEN;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "org.talkappmobile.dao.impl")
public class WordRepetitionProgressServiceImplIntegTest {

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

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        Word2Tokens anniversary = new Word2Tokens("anniversary", "anniversary", 3);

        for (int c = 0; c < 12; c++) {
            for (int i = 2; i <= 13; i++) {
                WordRepetitionProgressMapping exercise = new WordRepetitionProgressMapping();
                exercise.setSentenceIds("AWbgbq6hNEXFMlzHK5Ul#" + anniversary.getWord() + "#6");
                exercise.setStatus(WordSetProgressStatus.FINISHED.name());
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