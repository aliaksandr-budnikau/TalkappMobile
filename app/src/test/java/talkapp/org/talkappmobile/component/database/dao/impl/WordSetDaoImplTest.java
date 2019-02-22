package talkapp.org.talkappmobile.component.database.dao.impl;

import android.database.Cursor;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.sql.SQLException;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.impl.local.WordSetDaoImpl;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping.ID_FN;
import static talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping.STATUS_FN;
import static talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping.TOPIC_ID_FN;
import static talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping.TOP_FN;
import static talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping.TRAINING_EXPERIENCE_FN;
import static talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping.WORDS_FN;
import static talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping.WORD_SET_TABLE;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FIRST_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.SECOND_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.component.database.dao.impl")
public class WordSetDaoImplTest {

    private DatabaseHelper databaseHelper;
    private WordSetDao experienceDao;

    @Before
    public void setup() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        experienceDao = new WordSetDaoImpl(databaseHelper.getConnectionSource(), WordSetMapping.class);
    }

    @Before
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void createNewOrUpdate_ordinaryCaseOfCreation() {
        // setup
        WordSetMapping exp = new WordSetMapping();
        exp.setWords("words,words,words,words,words,words,words,words,words,words,words,words");
        exp.setTop(3);
        exp.setTopicId(String.valueOf(45));
        exp.setId(String.valueOf(1));
        exp.setTrainingExperience(3);
        exp.setMaxTrainingExperience(10);
        exp.setStatus(FIRST_CYCLE);

        // when
        experienceDao.createNewOrUpdate(exp);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_SET_TABLE), new String[]{});
        cursor.moveToNext();

        assertEquals(exp.getId(), cursor.getString(cursor.getColumnIndex(ID_FN)));
        assertEquals(exp.getTrainingExperience(), cursor.getInt(cursor.getColumnIndex(TRAINING_EXPERIENCE_FN)));
        assertEquals(exp.getStatus().name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(1, cursor.getCount());
    }

    @Test
    public void createNewOrUpdate_ordinaryCaseOfUpdate() {
        WordSetMapping exp;
        exp = new WordSetMapping();
        exp.setWords("words,words,words,words,words,words,words,words,words,words,words,words");
        exp.setTop(3);
        exp.setTopicId(String.valueOf(45));
        exp.setId(String.valueOf(1));
        exp.setTrainingExperience(0);
        exp.setStatus(FIRST_CYCLE);
        experienceDao.createNewOrUpdate(exp);

        exp = new WordSetMapping();
        exp.setWords("words,words,words,words,words,words,words,words,words,words,words,words");
        exp.setTop(3);
        exp.setTopicId(String.valueOf(45));
        exp.setId(String.valueOf(2));
        exp.setTrainingExperience(0);
        exp.setStatus(FIRST_CYCLE);
        experienceDao.createNewOrUpdate(exp);

        exp = new WordSetMapping();
        exp.setWords("words,words,words,words,words,words,words,words,words,words,words,words");
        exp.setTop(3);
        exp.setTopicId(String.valueOf(45));
        exp.setId(String.valueOf(2));
        exp.setTrainingExperience(1);
        exp.setStatus(SECOND_CYCLE);
        experienceDao.createNewOrUpdate(exp);

        exp = new WordSetMapping();
        exp.setWords("words,words,words,words,words,words,words,words,words,words,words,words");
        exp.setTop(3);
        exp.setTopicId(String.valueOf(45));
        exp.setId(String.valueOf(2));
        exp.setTrainingExperience(2);
        exp.setStatus(SECOND_CYCLE);
        experienceDao.createNewOrUpdate(exp);

        exp = new WordSetMapping();
        exp.setWords("words,words,words,words,words,words,words,words,words,words,words,words");
        exp.setTop(3);
        exp.setTopicId(String.valueOf(45));
        exp.setId(String.valueOf(2));
        exp.setTrainingExperience(3);
        exp.setStatus(FINISHED);
        experienceDao.createNewOrUpdate(exp);

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", WORD_SET_TABLE, 1), new String[]{});
        cursor.moveToNext();

        assertEquals(1, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(TRAINING_EXPERIENCE_FN)));
        assertEquals(FIRST_CYCLE.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", WORD_SET_TABLE, 2), new String[]{});
        cursor.moveToNext();

        assertEquals(2, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(3, cursor.getInt(cursor.getColumnIndex(TRAINING_EXPERIENCE_FN)));
        assertEquals(FINISHED.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_SET_TABLE), new String[]{});
        assertEquals(2, cursor.getCount());
    }

    @Test(expected = RuntimeException.class)
    public void createNewOrUpdate_statusNull() {
        WordSetMapping exp = new WordSetMapping();
        exp.setId(String.valueOf(1));
        exp.setTrainingExperience(3);
        exp.setMaxTrainingExperience(10);
        exp.setStatus(null);
        experienceDao.createNewOrUpdate(exp);
    }

    @Test
    public void createNewOrUpdate_onlyStatusNotNull() {
        // setup
        WordSetMapping exp = new WordSetMapping();
        exp.setWords("words,words,words,words,words,words,words,words,words,words,words,words");
        exp.setTop(3);
        exp.setTopicId(String.valueOf(45));
        exp.setStatus(FIRST_CYCLE);
        exp.setId("0");

        // when
        experienceDao.createNewOrUpdate(exp);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_SET_TABLE), new String[]{});
        cursor.moveToNext();

        assertEquals(0, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(TRAINING_EXPERIENCE_FN)));
        assertEquals(exp.getStatus().name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(1, cursor.getCount());
    }

    @Test
    public void findById_ordinaryCase() {
        String sql = format("INSERT INTO %s (%s,%s,%s,%s,%s,%s) VALUES ('%s','%s','%s','%s','%s','%s')", WORD_SET_TABLE,
                ID_FN, TOPIC_ID_FN, WORDS_FN, TOP_FN, TRAINING_EXPERIENCE_FN, STATUS_FN,
                1, "1", "words,words,words,words,words,words,words,words,words,words,words,words", 4, 0, "FINISHED");
        databaseHelper.getWritableDatabase().execSQL(sql);
        WordSetMapping exp = experienceDao.findById(1);

        assertEquals(String.valueOf(1), exp.getId());
        assertEquals(0, exp.getTrainingExperience());
        assertEquals(FINISHED, exp.getStatus());
    }

    @Test
    public void findById_ordinaryCaseNothingFound() {
        assertNull(experienceDao.findById(1));
    }
}