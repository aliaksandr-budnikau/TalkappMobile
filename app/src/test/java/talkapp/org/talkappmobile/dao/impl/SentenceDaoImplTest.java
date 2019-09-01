package talkapp.org.talkappmobile.dao.impl;

import android.database.Cursor;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.BuildConfig;
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

    @Before
    public void setup() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        sentenceDao = new SentenceDaoImpl(databaseHelper.getConnectionSource(), SentenceMapping.class);
    }

    @Before
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void save_losingOfAlternatives() {
        //setup
        LinkedList<SentenceMapping> mappings = new LinkedList<>();
        SentenceMapping map1 = new SentenceMapping();
        map1.setId("dddd#genre#6");
        map1.setText("dddd#genre#6");
        map1.setTranslations("dddd#genre#6");
        map1.setTokens("dddd#genre#6");
        mappings.add(map1);
        SentenceMapping map2 = new SentenceMapping();
        map2.setId("ssss#genre#6");
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
        Map<String, List<SentenceMapping>> sentences = Whitebox.getInternalState(sentenceDao, "sentences");
        assertEquals(2, sentences.get("genre_6").size());
    }

    @Test
    public void save_losingOfAlternativesForExpressions() {
        //setup
        LinkedList<SentenceMapping> mappings = new LinkedList<>();
        SentenceMapping map1 = new SentenceMapping();
        map1.setId("dddd#look for#6");
        map1.setText("dddd#look for#6");
        map1.setTranslations("dddd#look for#6");
        map1.setTokens("dddd#look for#6");
        mappings.add(map1);
        SentenceMapping map2 = new SentenceMapping();
        map2.setId("ssss#look for#6");
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
        Map<String, List<SentenceMapping>> sentences = Whitebox.getInternalState(sentenceDao, "sentences");
        assertEquals(2, sentences.get("look for_6").size());
    }

    @Test
    public void findAllByWord_quotationMarkBug() {
        SentenceMapping map = new SentenceMapping();
        String word = "it's actually a quite peculiar idea.";
        map.setId("ssss#" + word + "#6");
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

        sentenceDao.save(asList(map1, map2));
        List<SentenceMapping> allByIds = sentenceDao.findAllByIds(new String[]{map1.getId(), map2.getId()});
        assertEquals(2, allByIds.size());
    }
}