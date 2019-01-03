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
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping.ID_FN;
import static talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping.MAX_TRAINING_EXPERIENCE_FN;
import static talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping.STATUS_FN;
import static talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping.TRAINING_EXPERIENCE_FN;
import static talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping.WORD_SET_EXPERIENCE_TABLE;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.SECOND_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FIRST_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.component.database.dao.impl")
public class WordSetExperienceDaoImplTest {

    private DatabaseHelper databaseHelper;
    private WordSetExperienceDao experienceDao;

    @Before
    public void setup() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        experienceDao = new WordSetExperienceDaoImpl(databaseHelper.getConnectionSource(), WordSetExperienceMapping.class);
    }

    @Before
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void createNewOrUpdate_ordinaryCaseOfCreation() {
        // setup
        WordSetExperienceMapping exp = new WordSetExperienceMapping(1, 3, 10, FIRST_CYCLE);

        // when
        experienceDao.createNewOrUpdate(exp);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_SET_EXPERIENCE_TABLE), new String[]{});
        cursor.moveToNext();

        assertEquals(exp.getId(), cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(exp.getTrainingExperience(), cursor.getInt(cursor.getColumnIndex(TRAINING_EXPERIENCE_FN)));
        assertEquals(exp.getMaxTrainingExperience(), cursor.getInt(cursor.getColumnIndex(MAX_TRAINING_EXPERIENCE_FN)));
        assertEquals(exp.getStatus().name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(1, cursor.getCount());
    }

    @Test
    public void createNewOrUpdate_ordinaryCaseOfUpdate() {
        WordSetExperienceMapping exp;
        exp = new WordSetExperienceMapping(1, 0, 3, FIRST_CYCLE);
        experienceDao.createNewOrUpdate(exp);
        exp = new WordSetExperienceMapping(2, 0, 3, FIRST_CYCLE);
        experienceDao.createNewOrUpdate(exp);
        exp = new WordSetExperienceMapping(2, 1, 3, SECOND_CYCLE);
        experienceDao.createNewOrUpdate(exp);
        exp = new WordSetExperienceMapping(2, 2, 3, SECOND_CYCLE);
        experienceDao.createNewOrUpdate(exp);
        exp = new WordSetExperienceMapping(2, 3, 3, FINISHED);
        experienceDao.createNewOrUpdate(exp);

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", WORD_SET_EXPERIENCE_TABLE, 1), new String[]{});
        cursor.moveToNext();

        assertEquals(1, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(TRAINING_EXPERIENCE_FN)));
        assertEquals(3, cursor.getInt(cursor.getColumnIndex(MAX_TRAINING_EXPERIENCE_FN)));
        assertEquals(FIRST_CYCLE.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", WORD_SET_EXPERIENCE_TABLE, 2), new String[]{});
        cursor.moveToNext();

        assertEquals(2, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(3, cursor.getInt(cursor.getColumnIndex(TRAINING_EXPERIENCE_FN)));
        assertEquals(3, cursor.getInt(cursor.getColumnIndex(MAX_TRAINING_EXPERIENCE_FN)));
        assertEquals(FINISHED.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_SET_EXPERIENCE_TABLE), new String[]{});
        assertEquals(2, cursor.getCount());
    }

    @Test(expected = RuntimeException.class)
    public void createNewOrUpdate_statusNull() {
        WordSetExperienceMapping exp = new WordSetExperienceMapping(1, 3, 10, null);
        experienceDao.createNewOrUpdate(exp);
    }

    @Test
    public void createNewOrUpdate_onlyStatusNotNull() {
        // setup
        WordSetExperienceMapping exp = new WordSetExperienceMapping();
        exp.setStatus(FIRST_CYCLE);

        // when
        experienceDao.createNewOrUpdate(exp);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_SET_EXPERIENCE_TABLE), new String[]{});
        cursor.moveToNext();

        assertEquals(0, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(TRAINING_EXPERIENCE_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(MAX_TRAINING_EXPERIENCE_FN)));
        assertEquals(exp.getStatus().name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(1, cursor.getCount());
    }

    @Test
    public void findById_ordinaryCase() {
        String sql = format("INSERT INTO %s (%s,%s,%s,%s) VALUES ('%s','%s','%s','%s')", WORD_SET_EXPERIENCE_TABLE,
                ID_FN, TRAINING_EXPERIENCE_FN, MAX_TRAINING_EXPERIENCE_FN, STATUS_FN,
                1, 0, 10, "FINISHED");
        databaseHelper.getWritableDatabase().execSQL(sql);
        WordSetExperienceMapping exp = experienceDao.findById(1);

        assertEquals(1, exp.getId());
        assertEquals(0, exp.getTrainingExperience());
        assertEquals(10, exp.getMaxTrainingExperience());
        assertEquals(FINISHED, exp.getStatus());
    }

    @Test
    public void findById_ordinaryCaseNothingFound() {
        assertNull(experienceDao.findById(1));
    }
}