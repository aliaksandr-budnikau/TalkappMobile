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
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import talkapp.org.talkappmobile.BuildConfig;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
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
}