package talkapp.org.talkappmobile.dao.impl;

import android.database.Cursor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static talkapp.org.talkappmobile.mappings.SentenceMapping.SENTENCE_TABLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl.local")
public class SentenceDaoImplTest {

    private DatabaseHelper databaseHelper;
    private SentenceDao sentenceDao;
    private DaoHelper daoHelper;

    @Before
    public void setup() throws SQLException {
        daoHelper = new DaoHelper();
        databaseHelper = daoHelper.getDatabaseHelper();
        sentenceDao = daoHelper.getSentenceDao();
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
    }

    @Test
    public void save_losingOfAlternatives() {
        //setup
        LinkedList<SentenceMapping> mappings = new LinkedList<>();
        SentenceMapping map1 = new SentenceMapping();
        map1.setId("{\"sentenceId\":\"dddd\",\"word\":\"genre\",\"lengthInWords\":6}");
        map1.setText("dddd#genre#6");
        map1.setTranslations("dddd#genre#6");
        map1.setTokens("dddd#genre#6");
        mappings.add(map1);
        SentenceMapping map2 = new SentenceMapping();
        map2.setId("{\"sentenceId\":\"ssss\",\"word\":\"genre\",\"lengthInWords\":6}");
        map2.setText("dddd#genre#6");
        map2.setTranslations("dddd#genre#6");
        map2.setTokens("dddd#genre#6");
        mappings.add(map2);

        // when
        sentenceDao.save(mappings);
        sentenceDao.save(mappings);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", SENTENCE_TABLE), new String[]{});
        assertEquals(2, cursor.getCount());
    }

    @Test
    public void save_losingOfAlternativesForExpressions() {
        //setup
        LinkedList<SentenceMapping> mappings = new LinkedList<>();
        SentenceMapping map1 = new SentenceMapping();
        map1.setId("{\"sentenceId\":\"dddd\",\"word\":\"look for\",\"lengthInWords\":6}");
        map1.setText("dddd#look for#6");
        map1.setTranslations("dddd#look for#6");
        map1.setTokens("dddd#look for#6");
        mappings.add(map1);
        SentenceMapping map2 = new SentenceMapping();
        map2.setId("{\"sentenceId\":\"ssss\",\"word\":\"look for\",\"lengthInWords\":6}");
        map2.setText("dddd#look for#6");
        map2.setTranslations("dddd#look for#6");
        map2.setTokens("dddd#look for#6");
        mappings.add(map2);

        // when
        sentenceDao.save(mappings);
        sentenceDao.save(mappings);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", SENTENCE_TABLE), new String[]{});
        assertEquals(2, cursor.getCount());
    }

    @Test
    public void findAllByWord_quotationMarkBug() {
        SentenceMapping map = new SentenceMapping();
        String word = "it's actually a quite peculiar idea.";
        map.setId("{\"sentenceId\":\"ssss\",\"word\":\"" + word + "\",\"lengthInWords\":6}");
        map.setText("ssss#" + word + "#6");
        map.setTranslations("ssss#" + word + "#6");
        map.setTokens("ssss#" + word + "#6");

        sentenceDao.save(singletonList(map));
        List<SentenceMapping> allByWord = sentenceDao.findAllByWord(word, 6);
        assertFalse(allByWord.isEmpty());
    }

    @Test
    public void findAllByIds_quotationMarkBug() {
        SentenceMapping map1 = new SentenceMapping();
        String word1 = "it's actually a quite peculiar idea1.";
        map1.setId("ssss#" + word1 + "#6");
        map1.setText("ssss#" + word1 + "#6");
        map1.setTranslations("ssss#" + word1 + "#6");
        map1.setTokens("ssss#" + word1 + "#6");

        SentenceMapping map2 = new SentenceMapping();
        String word2 = "it's actually a quite peculiar idea2.";
        map2.setId("ssss#" + word2 + "#6");
        map2.setText("ssss#" + word2 + "#6");
        map2.setTranslations("ssss#" + word2 + "#6");
        map2.setTokens("ssss#" + word2 + "#6");

        SentenceMapping map3 = new SentenceMapping();
        String word3 = "{\"word\":\"It's a bit of a reach.\",\"sentenceId\":\"1570312811094\",\"lengthInWords\":6}";
        map3.setId(word3);
        map3.setText("ssss#" + word3 + "#6");
        map3.setTranslations("ssss#" + word3 + "#6");
        map3.setTokens("ssss#" + word3 + "#6");

        sentenceDao.save(asList(map1, map2, map3));
        List<SentenceMapping> allByIds = sentenceDao.findAllByIds(new String[]{map1.getId(), map2.getId(), map3.getId()});
        assertEquals(3, allByIds.size());
    }
}