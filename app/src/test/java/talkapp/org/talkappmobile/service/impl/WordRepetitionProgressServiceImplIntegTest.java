package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static talkapp.org.talkappmobile.model.RepetitionClass.LEARNED;
import static talkapp.org.talkappmobile.model.RepetitionClass.NEW;
import static talkapp.org.talkappmobile.model.RepetitionClass.REPEATED;
import static talkapp.org.talkappmobile.model.RepetitionClass.SEEN;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class WordRepetitionProgressServiceImplIntegTest {

    private WordRepetitionProgressServiceImpl service;
    private WordRepetitionProgressDao exerciseDao;
    private ObjectMapper mapper;
    private DaoHelper daoHelper;

    @Before
    public void setUp() throws Exception {
        daoHelper = new DaoHelper();
        SentenceDao sentenceDao = mock(SentenceDao.class);
        WordSetDao wordSetDao = daoHelper.getWordSetDao();
        exerciseDao = daoHelper.getWordRepetitionProgressDao();
        mapper = new ObjectMapper();
        service = new WordRepetitionProgressServiceImpl(exerciseDao, wordSetDao, sentenceDao, mapper);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        int sourceWordSetId = 3;
        Word2Tokens anniversary = new Word2Tokens("anniversary", "anniversary", sourceWordSetId);
        WordSetMapping wordSetMapping = new WordSetMapping();
        wordSetMapping.setTopicId("" + sourceWordSetId);
        wordSetMapping.setId("" + sourceWordSetId);
        wordSetMapping.setWords(mapper.writeValueAsString(asList(anniversary)));
        wordSetDao.createNewOrUpdate(wordSetMapping);
        for (int c = 0; c < 12; c++) {
            for (int i = 2; i <= 13; i++) {
                WordRepetitionProgressMapping exercise = new WordRepetitionProgressMapping();
                exercise.setSentenceIds("AWbgbq6hNEXFMlzHK5Ul");
                exercise.setStatus(WordSetProgressStatus.FINISHED.name());
                cal.add(Calendar.HOUR, -2 * 24 * i);
                exercise.setUpdatedDate(cal.getTime());
                exercise.setRepetitionCounter(c);
                exercise.setWordSetId(sourceWordSetId);
                exerciseDao.createNewOrUpdate(exercise);
            }
        }
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
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