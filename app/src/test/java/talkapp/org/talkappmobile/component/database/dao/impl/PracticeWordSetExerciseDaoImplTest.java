package talkapp.org.talkappmobile.component.database.dao.impl;

import android.database.Cursor;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.getInstance;
import static okhttp3.internal.Util.UTC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.CURRENT_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.ID_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.PRACTICE_WORD_SET_EXERCISE_TABLE;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.REPETITION_COUNTER_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.SENTENCE_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.STATUS_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.UPDATED_DATE_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.WORD_FN;
import static talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping.WORD_SET_ID_FN;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.SECOND_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetExperienceStatus.FIRST_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.component.database.dao.impl")
public class PracticeWordSetExerciseDaoImplTest {

    private DatabaseHelper databaseHelper;
    private PracticeWordSetExerciseDao exerciseDao;

    @Before
    public void setUp() throws Exception {
        databaseHelper = OpenHelperManager.getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
        exerciseDao = new PracticeWordSetExerciseDaoImpl(databaseHelper.getConnectionSource(), PracticeWordSetExerciseMapping.class);
    }

    @After
    public void tearDown() {
        OpenHelperManager.releaseHelper();
    }

    @Test
    public void createNewOrUpdate_ordinaryCaseOfCreation() {
        // setup
        PracticeWordSetExerciseMapping exe = new PracticeWordSetExerciseMapping(1, 2, "wordJSON", "sentenceJSON", FIRST_CYCLE, true);

        // when
        exerciseDao.createNewOrUpdate(exe);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", PRACTICE_WORD_SET_EXERCISE_TABLE), new String[]{});
        cursor.moveToNext();

        assertEquals(exe.getId(), cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(exe.getWordSetId(), cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals(exe.getWordJSON(), cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals(exe.getSentenceJSON(), cursor.getString(cursor.getColumnIndex(SENTENCE_FN)));
        assertEquals(exe.getStatus().name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(exe.isCurrent() ? 1 : 0, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));

        assertEquals(1, cursor.getCount());
    }

    @Test
    public void createNewOrUpdate_ordinaryCaseOfUpdate() {
        PracticeWordSetExerciseMapping exe;
        exe = new PracticeWordSetExerciseMapping(1, 2, "wordJSON1", "sentenceJSON1", FIRST_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new PracticeWordSetExerciseMapping(2, 2, "wordJSON1", "sentenceJSON1", FIRST_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new PracticeWordSetExerciseMapping(2, 2, "wordJSON2", "sentenceJSON2", SECOND_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new PracticeWordSetExerciseMapping(2, 2, "wordJSON3", "sentenceJSON3", FINISHED, false);
        exerciseDao.createNewOrUpdate(exe);

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", PRACTICE_WORD_SET_EXERCISE_TABLE, 1), new String[]{});
        cursor.moveToNext();

        assertEquals(1, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(2, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals("wordJSON1", cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals("sentenceJSON1", cursor.getString(cursor.getColumnIndex(SENTENCE_FN)));
        assertEquals(FIRST_CYCLE.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));


        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", PRACTICE_WORD_SET_EXERCISE_TABLE, 2), new String[]{});
        cursor.moveToNext();

        assertEquals(2, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(2, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals("wordJSON3", cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals("sentenceJSON3", cursor.getString(cursor.getColumnIndex(SENTENCE_FN)));
        assertEquals(FINISHED.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", PRACTICE_WORD_SET_EXERCISE_TABLE, 1), new String[]{});
        cursor.moveToNext();
        assertEquals(2, cursor.getCount());
    }

    @Test(expected = RuntimeException.class)
    public void createNewOrUpdate_wordNull() {
        PracticeWordSetExerciseMapping exe = new PracticeWordSetExerciseMapping(1, 2, null, "sentenceJSON", FIRST_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
    }

    @Test
    public void createNewOrUpdate_sentenceNull() {
        PracticeWordSetExerciseMapping exe = new PracticeWordSetExerciseMapping(1, 2, "wordJSON", null, FIRST_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
    }

    @Test(expected = RuntimeException.class)
    public void createNewOrUpdate_statusNull() {
        PracticeWordSetExerciseMapping exe = new PracticeWordSetExerciseMapping(1, 2, "wordJSON", "sentenceJSON", null, true);
        exerciseDao.createNewOrUpdate(exe);
    }

    @Test
    public void createNewOrUpdate_onlyWordSentenceStatusNotNull() {
        // setup
        PracticeWordSetExerciseMapping exe = new PracticeWordSetExerciseMapping();
        exe.setWordJSON("wordJSON");
        exe.setSentenceJSON("sentenceJSON");
        exe.setStatus(FIRST_CYCLE);
        exe.setUpdatedDate(getInstance(UTC).getTime());

        // when
        exerciseDao.createNewOrUpdate(exe);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", PRACTICE_WORD_SET_EXERCISE_TABLE), new String[]{});
        cursor.moveToNext();

        assertEquals(1, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals(exe.getWordJSON(), cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals(exe.getSentenceJSON(), cursor.getString(cursor.getColumnIndex(SENTENCE_FN)));
        assertEquals(exe.getStatus().name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));

        assertEquals(1, cursor.getCount());
    }

    @Test
    public void cleanByWordSetId_nothingToDeleteEmptyDB() {
        exerciseDao.cleanByWordSetId(1);
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", PRACTICE_WORD_SET_EXERCISE_TABLE), new String[]{});
        assertEquals(0, cursor.getCount());
    }

    @Test
    public void cleanByWordSetId_nothingToDeleteNotEmptyDB() {
        PracticeWordSetExerciseMapping exe;
        exe = new PracticeWordSetExerciseMapping(1, 1, "wordJSON1", "sentenceJSON1", FIRST_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new PracticeWordSetExerciseMapping(2, 2, "wordJSON1", "sentenceJSON1", FIRST_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new PracticeWordSetExerciseMapping(4, 4, "wordJSON2", "sentenceJSON2", SECOND_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new PracticeWordSetExerciseMapping(5, 5, "wordJSON3", "sentenceJSON3", FINISHED, false);
        exerciseDao.createNewOrUpdate(exe);

        exerciseDao.cleanByWordSetId(3);

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE wordSetId = %s;", PRACTICE_WORD_SET_EXERCISE_TABLE, 3), new String[]{});
        assertEquals(0, cursor.getCount());

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", PRACTICE_WORD_SET_EXERCISE_TABLE), new String[]{});
        assertEquals(4, cursor.getCount());
    }

    @Test
    public void cleanByWordSetId_ordinaryCase() {
        PracticeWordSetExerciseMapping exe;
        exe = new PracticeWordSetExerciseMapping(1, 2, "wordJSON1", "sentenceJSON1", FIRST_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new PracticeWordSetExerciseMapping(2, 3, "wordJSON1", "sentenceJSON1", FIRST_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new PracticeWordSetExerciseMapping(4, 4, "wordJSON2", "sentenceJSON2", SECOND_CYCLE, true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new PracticeWordSetExerciseMapping(5, 3, "wordJSON3", "sentenceJSON3", FINISHED, false);
        exerciseDao.createNewOrUpdate(exe);

        exerciseDao.cleanByWordSetId(3);

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE wordSetId = %s;", PRACTICE_WORD_SET_EXERCISE_TABLE, 3), new String[]{});
        assertEquals(0, cursor.getCount());

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", PRACTICE_WORD_SET_EXERCISE_TABLE), new String[]{});
        assertEquals(2, cursor.getCount());
    }

    @Test
    public void createAll_nothingToCreate() {
        // setup
        LinkedList<PracticeWordSetExerciseMapping> words = new LinkedList<>();

        // when
        exerciseDao.createAll(words);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", PRACTICE_WORD_SET_EXERCISE_TABLE), new String[]{});
        assertEquals(0, cursor.getCount());
    }

    @Test
    public void createAll_createOne() {
        // setup
        List<PracticeWordSetExerciseMapping> words = asList(new PracticeWordSetExerciseMapping(2, 5, "wordJSON", "sentenceJSON", FINISHED, true));

        // when
        exerciseDao.createAll(words);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", PRACTICE_WORD_SET_EXERCISE_TABLE), new String[]{});
        cursor.moveToNext();

        assertEquals(1, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(5, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals("wordJSON", cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals("sentenceJSON", cursor.getString(cursor.getColumnIndex(SENTENCE_FN)));
        assertEquals(FINISHED.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));

        assertEquals(1, cursor.getCount());
    }

    @Test
    public void createAll_createTwo() {
        List<PracticeWordSetExerciseMapping> words = asList(
                new PracticeWordSetExerciseMapping(1, 2, "wordJSON1", "sentenceJSON1", FIRST_CYCLE, true),
                new PracticeWordSetExerciseMapping(2, 2, "wordJSON3", "sentenceJSON3", FINISHED, false)
        );
        exerciseDao.createAll(words);

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", PRACTICE_WORD_SET_EXERCISE_TABLE, 1), new String[]{});
        cursor.moveToNext();

        assertEquals(1, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(2, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals("wordJSON1", cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals("sentenceJSON1", cursor.getString(cursor.getColumnIndex(SENTENCE_FN)));
        assertEquals(FIRST_CYCLE.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));


        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", PRACTICE_WORD_SET_EXERCISE_TABLE, 2), new String[]{});
        cursor.moveToNext();

        assertEquals(2, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(2, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals("wordJSON3", cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals("sentenceJSON3", cursor.getString(cursor.getColumnIndex(SENTENCE_FN)));
        assertEquals(FINISHED.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", PRACTICE_WORD_SET_EXERCISE_TABLE, 1), new String[]{});
        cursor.moveToNext();
        assertEquals(2, cursor.getCount());
    }

    @Test
    public void findByStatusAndByWordSetId_nothingToReturn() {
        List<PracticeWordSetExerciseMapping> result = exerciseDao.findByStatusAndByWordSetId(FINISHED, 4);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByStatusAndByWordSetId_ordinaryCase() {
        insertExercise(1, 5, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, "wordJSON4", "sentenceJSON2", FINISHED.name(), "1");
        insertExercise(3, 2, "wordJSON42", "sentenceJSON22", FINISHED.name(), "0");

        List<PracticeWordSetExerciseMapping> result = exerciseDao.findByStatusAndByWordSetId(FINISHED, 2);
        assertEquals(2, result.size());

        assertEquals(22, result.get(1).getId());
        assertEquals(2, result.get(1).getWordSetId());
        assertEquals("wordJSON4", result.get(1).getWordJSON());
        assertEquals("sentenceJSON2", result.get(1).getSentenceJSON());
        assertEquals(FINISHED, result.get(1).getStatus());
        assertEquals(true, result.get(1).isCurrent());

        assertEquals(3, result.get(0).getId());
        assertEquals(2, result.get(0).getWordSetId());
        assertEquals("wordJSON42", result.get(0).getWordJSON());
        assertEquals("sentenceJSON22", result.get(0).getSentenceJSON());
        assertEquals(FINISHED, result.get(0).getStatus());
        assertEquals(false, result.get(0).isCurrent());
    }

    @Test
    public void findByStatusAndByWordSetId_ordinaryCaseButNothingReturn() {
        insertExercise(1, 5, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, "wordJSON4", "sentenceJSON2", FINISHED.name(), "1");
        insertExercise(3, 2, "wordJSON42", "sentenceJSON22", FINISHED.name(), "0");

        List<PracticeWordSetExerciseMapping> result = exerciseDao.findByStatusAndByWordSetId(FINISHED, 4);
        assertEquals(0, result.size());
    }

    @Test
    public void findByCurrentAndByWordSetId_nothingToReturn() {
        List<PracticeWordSetExerciseMapping> result = exerciseDao.findByCurrentAndByWordSetId(4);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByCurrentAndByWordSetId_ordinaryCase() {
        insertExercise(1, 5, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, "wordJSON4", "sentenceJSON2", FINISHED.name(), "1");
        insertExercise(3, 2, "wordJSON42", "sentenceJSON22", FINISHED.name(), "1");

        List<PracticeWordSetExerciseMapping> result = exerciseDao.findByCurrentAndByWordSetId(2);
        assertEquals(2, result.size());

        assertEquals(22, result.get(1).getId());
        assertEquals(2, result.get(1).getWordSetId());
        assertEquals("wordJSON4", result.get(1).getWordJSON());
        assertEquals("sentenceJSON2", result.get(1).getSentenceJSON());
        assertEquals(FINISHED, result.get(1).getStatus());
        assertEquals(true, result.get(1).isCurrent());

        assertEquals(3, result.get(0).getId());
        assertEquals(2, result.get(0).getWordSetId());
        assertEquals("wordJSON42", result.get(0).getWordJSON());
        assertEquals("sentenceJSON22", result.get(0).getSentenceJSON());
        assertEquals(FINISHED, result.get(0).getStatus());
        assertEquals(true, result.get(0).isCurrent());
    }


    @Test
    public void findByCurrentAndByWordSetId_ordinaryCaseButNothingReturn() {
        insertExercise(1, 5, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, "wordJSON4", "sentenceJSON2", FINISHED.name(), "0");
        insertExercise(3, 2, "wordJSON42", "sentenceJSON22", FINISHED.name(), "0");

        List<PracticeWordSetExerciseMapping> result = exerciseDao.findByCurrentAndByWordSetId(2);
        assertEquals(0, result.size());
    }


    @Test
    public void findByWordAndWordSetId_nothingToReturn() {
        List<PracticeWordSetExerciseMapping> result = exerciseDao.findByWordAndWordSetId("dsdsds", 4);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByWordAndWordSetId_ordinaryCase() {
        insertExercise(1, 5, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, "wordJSON4", "sentenceJSON2", FINISHED.name(), "1");
        insertExercise(3, 2, "wordJSON4", "sentenceJSON22", FINISHED.name(), "1");

        List<PracticeWordSetExerciseMapping> result = exerciseDao.findByWordAndWordSetId("wordJSON4", 2);
        assertEquals(2, result.size());

        assertEquals(22, result.get(1).getId());
        assertEquals(2, result.get(1).getWordSetId());
        assertEquals("wordJSON4", result.get(1).getWordJSON());
        assertEquals("sentenceJSON2", result.get(1).getSentenceJSON());
        assertEquals(FINISHED, result.get(1).getStatus());
        assertEquals(true, result.get(1).isCurrent());

        assertEquals(3, result.get(0).getId());
        assertEquals(2, result.get(0).getWordSetId());
        assertEquals("wordJSON4", result.get(0).getWordJSON());
        assertEquals("sentenceJSON22", result.get(0).getSentenceJSON());
        assertEquals(FINISHED, result.get(0).getStatus());
        assertEquals(true, result.get(0).isCurrent());
    }


    @Test
    public void findByWordAndWordSetId_ordinaryCaseButNothingReturn() {
        insertExercise(1, 5, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, "wordJSON4", "sentenceJSON2", FINISHED.name(), "0");
        insertExercise(3, 2, "wordJSON42", "sentenceJSON22", FINISHED.name(), "0");

        List<PracticeWordSetExerciseMapping> result = exerciseDao.findByWordAndWordSetId("wordJSONsdds4", 2);
        assertEquals(0, result.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_emptyDB() {
        // setup
        Calendar instance = getInstance(UTC);
        instance.add(HOUR, -2);

        // when
        instance = getInstance(UTC);
        List<PracticeWordSetExerciseMapping> result = exerciseDao.findFinishedWordSetsSortByUpdatedDate(2, instance.getTime());

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyNewExercise() {
        // setup
        Calendar instance = getInstance(UTC);
        instance.add(MINUTE, -30);
        insertExercise(1, 5, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -119);
        insertExercise(2, 5, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());

        // when
        instance = getInstance(UTC);
        instance.add(HOUR, -2);
        List<PracticeWordSetExerciseMapping> result = exerciseDao.findFinishedWordSetsSortByUpdatedDate(2, instance.getTime());

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyOneOldExercise() {
        // setup
        Calendar instance = getInstance(UTC);
        instance.add(MINUTE, -30);
        insertExercise(1, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -119);
        insertExercise(2, 2, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -121);
        Date expectedDate = instance.getTime();
        insertExercise(3, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), "0", instance.getTime());

        // when
        instance = getInstance(UTC);
        instance.add(HOUR, -2);
        List<PracticeWordSetExerciseMapping> result = exerciseDao.findFinishedWordSetsSortByUpdatedDate(1, instance.getTime());

        // then
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getId());
        assertEquals(3, result.get(0).getWordSetId());
        assertEquals("wordJSON3", result.get(0).getWordJSON());
        assertEquals("sentenceJSON3", result.get(0).getSentenceJSON());
        assertEquals(FINISHED, result.get(0).getStatus());
        assertEquals(false, result.get(0).isCurrent());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyOneOldExerciseButNotFINISHED() {
        // setup
        Calendar instance = getInstance(UTC);
        instance.add(MINUTE, -30);
        insertExercise(1, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -119);
        insertExercise(2, 2, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -121);
        Date expectedDate = instance.getTime();
        insertExercise(3, 3, "wordJSON3", "sentenceJSON3", FIRST_CYCLE.name(), "0", instance.getTime());

        // when
        instance = getInstance(UTC);
        instance.add(HOUR, -2);
        List<PracticeWordSetExerciseMapping> result = exerciseDao.findFinishedWordSetsSortByUpdatedDate(2, instance.getTime());

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyOneOldExerciseButLimit0() {
        // setup
        Calendar instance = getInstance(UTC);
        instance.add(MINUTE, -30);
        insertExercise(1, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -119);
        insertExercise(2, 2, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -121);
        Date expectedDate = instance.getTime();
        insertExercise(3, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), "0", instance.getTime());

        // when
        instance = getInstance(UTC);
        instance.add(HOUR, -2);
        List<PracticeWordSetExerciseMapping> result = exerciseDao.findFinishedWordSetsSortByUpdatedDate(0, instance.getTime());

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyTreeOldExercise() {
        // setup
        Calendar instance = getInstance(UTC);
        instance.add(MINUTE, -119);
        insertExercise(2, 2, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -30);
        insertExercise(1, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -121);
        insertExercise(3, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), "0", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -1021);
        insertExercise(5, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), "0", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -131);
        insertExercise(4, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), "0", instance.getTime());

        // when
        instance = getInstance(UTC);
        instance.add(HOUR, -2);
        List<PracticeWordSetExerciseMapping> result = exerciseDao.findFinishedWordSetsSortByUpdatedDate(5, instance.getTime());

        // then
        assertEquals(3, result.size());
        Iterator<PracticeWordSetExerciseMapping> iterator = result.iterator();
        PracticeWordSetExerciseMapping current = iterator.next();
        while (iterator.hasNext()) {
            assertEquals(FINISHED, current.getStatus());
            Date olderDate = current.getUpdatedDate();
            current = iterator.next();
            assertTrue(olderDate.after(current.getUpdatedDate()));
        }
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate() {
        Calendar instance = getInstance(UTC);
        instance.add(HOUR, -1);
        insertExercise(1, 5, "wordJSON0", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(HOUR, -3);
        insertExercise(2, 5, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -130);
        insertExercise(3, 5, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = getInstance(UTC);
        instance.add(MINUTE, -140);
        insertExercise(4, 5, "wordJSON3", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());

        instance = getInstance(UTC);
        instance.add(HOUR, -2);
        List<PracticeWordSetExerciseMapping> result = exerciseDao.findFinishedWordSetsSortByUpdatedDate(2, instance.getTime());
        assertEquals(2, result.size());
    }

    private void insertExercise(int id, int wordSetId, String word, String sentence, String status, String current) {
        insertExercise(id, wordSetId, word, sentence, status, current, new Date(), 0);
    }

    private void insertExercise(int id, int wordSetId, String word, String sentence, String status, String current, Date updatedDate) {
        insertExercise(id, wordSetId, word, sentence, status, current, updatedDate, 0);
    }

    private void insertExercise(int id, int wordSetId, String word, String sentence, String status, String current, Date updatedDate, int repCount) {
        String sql = format("INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s');", PRACTICE_WORD_SET_EXERCISE_TABLE,
                ID_FN, WORD_SET_ID_FN, WORD_FN, SENTENCE_FN, STATUS_FN, CURRENT_FN, UPDATED_DATE_FN, REPETITION_COUNTER_FN,
                id, wordSetId, word, sentence, status, current, updatedDate.getTime(), repCount);
        databaseHelper.getWritableDatabase().execSQL(sql);
    }
}