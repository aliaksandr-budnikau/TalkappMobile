package talkapp.org.talkappmobile.dao.impl;

import android.database.Cursor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import talkapp.org.talkappmobile.BuildConfig;
import talkapp.org.talkappmobile.DaoHelper;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.CURRENT_FN;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.ID_FN;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.REPETITION_COUNTER_FN;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.SENTENCE_IDS_FN;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.STATUS_FN;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.UPDATED_DATE_FN;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.WORD_FN;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.WORD_INDEX_FN;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.WORD_REPETITION_PROGRESS_TABLE;
import static talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping.WORD_SET_ID_FN;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FINISHED;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.FIRST_CYCLE;
import static talkapp.org.talkappmobile.model.WordSetProgressStatus.SECOND_CYCLE;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = {LOLLIPOP}, packageName = "talkapp.org.talkappmobile.dao.impl")
public class WordRepetitionProgressDaoImplTest {

    private DatabaseHelper databaseHelper;
    private WordRepetitionProgressDao exerciseDao;
    private DaoHelper daoHelper;

    @Before
    public void setUp() throws Exception {
        daoHelper = new DaoHelper();
        databaseHelper = daoHelper.getDatabaseHelper();
        exerciseDao = daoHelper.getWordRepetitionProgressDao();
    }

    @After
    public void tearDown() {
        daoHelper.releaseHelper();
    }

    @Test
    public void createNewOrUpdate_ordinaryCaseOfCreation() {
        // setup
        WordRepetitionProgressMapping exe = new WordRepetitionProgressMapping(1, 2, 1, "wordJSON", "sentenceJSON", FIRST_CYCLE.name(), true);

        // when
        exerciseDao.createNewOrUpdate(exe);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_REPETITION_PROGRESS_TABLE), new String[]{});
        cursor.moveToNext();

        assertEquals(exe.getId(), cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(exe.getWordSetId(), cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals(exe.getWordJSON(), cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals(exe.getWordIndex(), cursor.getInt(cursor.getColumnIndex(WORD_INDEX_FN)));
        assertEquals(exe.getSentenceIds(), cursor.getString(cursor.getColumnIndex(SENTENCE_IDS_FN)));
        assertEquals(exe.getStatus(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(exe.isCurrent() ? 1 : 0, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));

        assertEquals(1, cursor.getCount());
    }

    @Test
    public void createNewOrUpdate_ordinaryCaseOfUpdate() {
        WordRepetitionProgressMapping exe;
        exe = new WordRepetitionProgressMapping(1, 2, 1, "wordJSON1", "sentenceJSON1", FIRST_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new WordRepetitionProgressMapping(2, 2, 1, "wordJSON1", "sentenceJSON1", FIRST_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new WordRepetitionProgressMapping(2, 2, 2, "wordJSON2", "sentenceJSON2", SECOND_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new WordRepetitionProgressMapping(2, 2, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), false);
        exerciseDao.createNewOrUpdate(exe);

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", WORD_REPETITION_PROGRESS_TABLE, 1), new String[]{});
        cursor.moveToNext();

        assertEquals(1, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(2, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals("wordJSON1", cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(WORD_INDEX_FN)));
        assertEquals("sentenceJSON1", cursor.getString(cursor.getColumnIndex(SENTENCE_IDS_FN)));
        assertEquals(FIRST_CYCLE.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));


        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", WORD_REPETITION_PROGRESS_TABLE, 2), new String[]{});
        cursor.moveToNext();

        assertEquals(2, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(2, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals("wordJSON3", cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals(3, cursor.getInt(cursor.getColumnIndex(WORD_INDEX_FN)));
        assertEquals("sentenceJSON3", cursor.getString(cursor.getColumnIndex(SENTENCE_IDS_FN)));
        assertEquals(FINISHED.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_REPETITION_PROGRESS_TABLE, 1), new String[]{});
        cursor.moveToNext();
        assertEquals(2, cursor.getCount());
    }

    @Test(expected = RuntimeException.class)
    public void createNewOrUpdate_wordNull() {
        WordRepetitionProgressMapping exe = new WordRepetitionProgressMapping(1, 2, -1, null, "sentenceJSON", FIRST_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
    }

    @Test
    public void createNewOrUpdate_sentenceNull() {
        WordRepetitionProgressMapping exe = new WordRepetitionProgressMapping(1, 2, 1, "wordJSON", null, FIRST_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
    }

    @Test(expected = RuntimeException.class)
    public void createNewOrUpdate_statusNull() {
        WordRepetitionProgressMapping exe = new WordRepetitionProgressMapping(1, 2, 1, "wordJSON", "sentenceJSON", null, true);
        exerciseDao.createNewOrUpdate(exe);
    }

    @Test
    public void createNewOrUpdate_onlyWordSentenceStatusNotNull() {
        // setup
        WordRepetitionProgressMapping exe = new WordRepetitionProgressMapping();
        exe.setWordJSON("wordJSON");
        exe.setSentenceIds("sentenceId");
        exe.setStatus(FIRST_CYCLE.name());
        exe.setUpdatedDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime());

        // when
        exerciseDao.createNewOrUpdate(exe);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_REPETITION_PROGRESS_TABLE), new String[]{});
        cursor.moveToNext();

        assertEquals(1, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals(exe.getWordJSON(), cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals(exe.getWordIndex(), cursor.getInt(cursor.getColumnIndex(WORD_INDEX_FN)));
        assertEquals(exe.getSentenceIds(), cursor.getString(cursor.getColumnIndex(SENTENCE_IDS_FN)));
        assertEquals(exe.getStatus(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));

        assertEquals(1, cursor.getCount());
    }

    @Test
    public void cleanByWordSetId_nothingToDeleteEmptyDB() {
        exerciseDao.cleanByWordSetId(1);
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_REPETITION_PROGRESS_TABLE), new String[]{});
        assertEquals(0, cursor.getCount());
    }

    @Test
    public void cleanByWordSetId_nothingToDeleteNotEmptyDB() {
        WordRepetitionProgressMapping exe;
        exe = new WordRepetitionProgressMapping(1, 1, 1, "wordJSON1", "sentenceJSON1", FIRST_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new WordRepetitionProgressMapping(2, 2, 1, "wordJSON1", "sentenceJSON1", FIRST_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new WordRepetitionProgressMapping(4, 4, 2, "wordJSON2", "sentenceJSON2", SECOND_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new WordRepetitionProgressMapping(5, 5, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), false);
        exerciseDao.createNewOrUpdate(exe);

        exerciseDao.cleanByWordSetId(3);

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE wordSetId = %s;", WORD_REPETITION_PROGRESS_TABLE, 3), new String[]{});
        assertEquals(0, cursor.getCount());

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_REPETITION_PROGRESS_TABLE), new String[]{});
        assertEquals(4, cursor.getCount());
    }

    @Test
    public void cleanByWordSetId_ordinaryCase() {
        WordRepetitionProgressMapping exe;
        exe = new WordRepetitionProgressMapping(1, 2, 1, "wordJSON1", "sentenceJSON1", FIRST_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new WordRepetitionProgressMapping(2, 3, 1, "wordJSON1", "sentenceJSON1", FIRST_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new WordRepetitionProgressMapping(4, 4, 2, "wordJSON2", "sentenceJSON2", SECOND_CYCLE.name(), true);
        exerciseDao.createNewOrUpdate(exe);
        exe = new WordRepetitionProgressMapping(5, 3, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), false);
        exerciseDao.createNewOrUpdate(exe);

        exerciseDao.cleanByWordSetId(3);

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE wordSetId = %s;", WORD_REPETITION_PROGRESS_TABLE, 3), new String[]{});
        assertEquals(0, cursor.getCount());

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_REPETITION_PROGRESS_TABLE), new String[]{});
        assertEquals(2, cursor.getCount());
    }

    @Test
    public void createAll_nothingToCreate() {
        // setup
        LinkedList<WordRepetitionProgressMapping> words = new LinkedList<>();

        // when
        exerciseDao.createAll(words);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_REPETITION_PROGRESS_TABLE), new String[]{});
        assertEquals(0, cursor.getCount());
    }

    @Test
    public void createAll_createOne() {
        // setup
        List<WordRepetitionProgressMapping> words = asList(new WordRepetitionProgressMapping(2, 5, 1, "wordJSON", "sentenceJSON", FINISHED.name(), true));

        // when
        exerciseDao.createAll(words);

        // then
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_REPETITION_PROGRESS_TABLE), new String[]{});
        cursor.moveToNext();

        assertEquals(1, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(5, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals("wordJSON", cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(WORD_INDEX_FN)));
        assertEquals("sentenceJSON", cursor.getString(cursor.getColumnIndex(SENTENCE_IDS_FN)));
        assertEquals(FINISHED.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));

        assertEquals(1, cursor.getCount());
    }

    @Test
    public void createAll_createTwo() {
        List<WordRepetitionProgressMapping> words = asList(
                new WordRepetitionProgressMapping(1, 2, 1, "wordJSON1", "sentenceJSON1", FIRST_CYCLE.name(), true),
                new WordRepetitionProgressMapping(2, 2, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), false)
        );
        exerciseDao.createAll(words);

        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", WORD_REPETITION_PROGRESS_TABLE, 1), new String[]{});
        cursor.moveToNext();

        assertEquals(1, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(2, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals("wordJSON1", cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(WORD_INDEX_FN)));
        assertEquals("sentenceJSON1", cursor.getString(cursor.getColumnIndex(SENTENCE_IDS_FN)));
        assertEquals(FIRST_CYCLE.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));


        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s WHERE id = %s;", WORD_REPETITION_PROGRESS_TABLE, 2), new String[]{});
        cursor.moveToNext();

        assertEquals(2, cursor.getInt(cursor.getColumnIndex(ID_FN)));
        assertEquals(2, cursor.getInt(cursor.getColumnIndex(WORD_SET_ID_FN)));
        assertEquals("wordJSON3", cursor.getString(cursor.getColumnIndex(WORD_FN)));
        assertEquals(3, cursor.getInt(cursor.getColumnIndex(WORD_INDEX_FN)));
        assertEquals("sentenceJSON3", cursor.getString(cursor.getColumnIndex(SENTENCE_IDS_FN)));
        assertEquals(FINISHED.name(), cursor.getString(cursor.getColumnIndex(STATUS_FN)));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex(CURRENT_FN)));

        cursor = databaseHelper.getReadableDatabase().rawQuery(format("SELECT * FROM %s;", WORD_REPETITION_PROGRESS_TABLE, 1), new String[]{});
        cursor.moveToNext();
        assertEquals(2, cursor.getCount());
    }

    @Test
    public void findByStatusAndByWordSetId_nothingToReturn() {
        List<WordRepetitionProgressMapping> result = exerciseDao.findByStatusAndByWordSetId(FINISHED.name(), 4);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByStatusAndByWordSetId_ordinaryCase() {
        insertExercise(1, 5, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, 4, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, 4, "wordJSON4", "sentenceJSON2", FINISHED.name(), "1");
        insertExercise(3, 2, 5, "wordJSON42", "sentenceJSON22", FINISHED.name(), "0");

        List<WordRepetitionProgressMapping> result = exerciseDao.findByStatusAndByWordSetId(FINISHED.name(), 2);
        assertEquals(2, result.size());

        assertEquals(22, result.get(1).getId());
        assertEquals(2, result.get(1).getWordSetId());
        assertEquals("wordJSON4", result.get(1).getWordJSON());
        assertEquals(4, result.get(1).getWordIndex());
        assertEquals("sentenceJSON2", result.get(1).getSentenceIds());
        assertEquals(FINISHED.name(), result.get(1).getStatus());
        assertEquals(true, result.get(1).isCurrent());

        assertEquals(3, result.get(0).getId());
        assertEquals(2, result.get(0).getWordSetId());
        assertEquals("wordJSON42", result.get(0).getWordJSON());
        assertEquals(5, result.get(0).getWordIndex());
        assertEquals("sentenceJSON22", result.get(0).getSentenceIds());
        assertEquals(FINISHED.name(), result.get(0).getStatus());
        assertEquals(false, result.get(0).isCurrent());
    }

    @Test
    public void findByStatusAndByWordSetId_ordinaryCaseButNothingReturn() {
        insertExercise(1, 5, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, 4, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, 4, "wordJSON4", "sentenceJSON2", FINISHED.name(), "1");
        insertExercise(3, 2, 5, "wordJSON42", "sentenceJSON22", FINISHED.name(), "0");

        List<WordRepetitionProgressMapping> result = exerciseDao.findByStatusAndByWordSetId(FINISHED.name(), 4);
        assertEquals(0, result.size());
    }

    @Test
    public void findByCurrentAndByWordSetId_nothingToReturn() {
        List<WordRepetitionProgressMapping> result = exerciseDao.findByCurrentAndByWordSetId(4);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByCurrentAndByWordSetId_ordinaryCase() {
        insertExercise(1, 5, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, 4, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, 4, "wordJSON4", "sentenceJSON2", FINISHED.name(), "1");
        insertExercise(3, 2, 5, "wordJSON42", "sentenceJSON22", FINISHED.name(), "1");

        List<WordRepetitionProgressMapping> result = exerciseDao.findByCurrentAndByWordSetId(2);
        assertEquals(2, result.size());

        assertEquals(22, result.get(1).getId());
        assertEquals(2, result.get(1).getWordSetId());
        assertEquals("wordJSON4", result.get(1).getWordJSON());
        assertEquals(4, result.get(1).getWordIndex());
        assertEquals("sentenceJSON2", result.get(1).getSentenceIds());
        assertEquals(FINISHED.name(), result.get(1).getStatus());
        assertEquals(true, result.get(1).isCurrent());

        assertEquals(3, result.get(0).getId());
        assertEquals(2, result.get(0).getWordSetId());
        assertEquals("wordJSON42", result.get(0).getWordJSON());
        assertEquals(5, result.get(0).getWordIndex());
        assertEquals("sentenceJSON22", result.get(0).getSentenceIds());
        assertEquals(FINISHED.name(), result.get(0).getStatus());
        assertEquals(true, result.get(0).isCurrent());
    }


    @Test
    public void findByCurrentAndByWordSetId_ordinaryCaseButNothingReturn() {
        insertExercise(1, 5, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, 4, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, 4, "wordJSON4", "sentenceJSON2", FINISHED.name(), "0");
        insertExercise(3, 2, 5, "wordJSON42", "sentenceJSON22", FINISHED.name(), "0");

        List<WordRepetitionProgressMapping> result = exerciseDao.findByCurrentAndByWordSetId(2);
        assertEquals(0, result.size());
    }


    @Test
    public void findByWordAndWordSetId_nothingToReturn() {
        List<WordRepetitionProgressMapping> result = exerciseDao.findByWordIndexAndWordSetId(1, 4);
        assertTrue(result.isEmpty());
        result = exerciseDao.findByWordIndexAndWordSetId(2, 4);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findByWordAndWordSetId_ordinaryCase() {
        insertExercise(1, 5, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, 4, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, 4, "wordJSON4", "sentenceJSON2", FINISHED.name(), "1");
        insertExercise(3, 2, 4, "wordJSON4", "sentenceJSON22", FINISHED.name(), "1");

        List<WordRepetitionProgressMapping> result = exerciseDao.findByWordIndexAndWordSetId(4, 2);
        assertEquals(2, result.size());

        assertEquals(22, result.get(1).getId());
        assertEquals(2, result.get(1).getWordSetId());
        assertEquals("wordJSON4", result.get(1).getWordJSON());
        assertEquals(4, result.get(1).getWordIndex());
        assertEquals("sentenceJSON2", result.get(1).getSentenceIds());
        assertEquals(FINISHED.name(), result.get(1).getStatus());
        assertEquals(true, result.get(1).isCurrent());

        assertEquals(3, result.get(0).getId());
        assertEquals(2, result.get(0).getWordSetId());
        assertEquals("wordJSON4", result.get(0).getWordJSON());
        assertEquals(4, result.get(0).getWordIndex());
        assertEquals("sentenceJSON22", result.get(0).getSentenceIds());
        assertEquals(FINISHED.name(), result.get(0).getStatus());
        assertEquals(true, result.get(0).isCurrent());
    }


    @Test
    public void findByWordAndWordSetId_ordinaryCaseButNothingReturn() {
        insertExercise(1, 5, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1");
        insertExercise(25, 2, 2, "wordJSON2", "sentenceJSON3", SECOND_CYCLE.name(), "0");
        insertExercise(232, 3, 4, "wordJSON4", "sentenceJSON2", FIRST_CYCLE.name(), "1");
        insertExercise(22, 2, 4, "wordJSON4", "sentenceJSON2", FINISHED.name(), "0");
        insertExercise(3, 2, 5, "wordJSON42", "sentenceJSON22", FINISHED.name(), "0");

        List<WordRepetitionProgressMapping> result = exerciseDao.findByWordIndexAndWordSetId(6, 2);
        assertEquals(0, result.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_emptyDB() {
        // setup
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(HOUR, -2);

        // when
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        List<WordRepetitionProgressMapping> result = exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(2, instance.getTime(), FINISHED.name());

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyNewExercise() {
        // setup
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -30);
        insertExercise(1, 5, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -119);
        insertExercise(2, 5, 2, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());

        // when
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(HOUR, -2);
        List<WordRepetitionProgressMapping> result = exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(2, instance.getTime(), FINISHED.name());

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyOneOldExercise() {
        // setup
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -30);
        insertExercise(1, 1, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -119);
        insertExercise(2, 2, 2, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -121);
        Date expectedDate = instance.getTime();
        insertExercise(3, 3, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), "0", instance.getTime());

        // when
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(HOUR, -2);
        List<WordRepetitionProgressMapping> result = exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(1, instance.getTime(), FINISHED.name());

        // then
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getId());
        assertEquals(3, result.get(0).getWordSetId());
        assertEquals("wordJSON3", result.get(0).getWordJSON());
        assertEquals(3, result.get(0).getWordIndex());
        assertEquals("sentenceJSON3", result.get(0).getSentenceIds());
        assertEquals(FINISHED.name(), result.get(0).getStatus());
        assertEquals(false, result.get(0).isCurrent());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyOneOldExerciseButNotFINISHED() {
        // setup
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -30);
        insertExercise(1, 1, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -119);
        insertExercise(2, 2, 2, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -121);
        Date expectedDate = instance.getTime();
        insertExercise(3, 3, 3, "wordJSON3", "sentenceJSON3", FIRST_CYCLE.name(), "0", instance.getTime());

        // when
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(HOUR, -2);
        List<WordRepetitionProgressMapping> result = exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(2, instance.getTime(), FINISHED.name());

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyOneOldExerciseButLimit0() {
        // setup
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -30);
        insertExercise(1, 1, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -119);
        insertExercise(2, 2, 2, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -121);
        Date expectedDate = instance.getTime();
        insertExercise(3, 3, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), "0", instance.getTime());

        // when
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(HOUR, -2);
        List<WordRepetitionProgressMapping> result = exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(0, instance.getTime(), FINISHED.name());

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate_notEmptyDBButOnlyTreeOldExercise() {
        // setup
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -119);
        insertExercise(2, 2, 2, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -30);
        insertExercise(1, 1, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -121);
        insertExercise(3, 3, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), "0", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -1021);
        insertExercise(5, 3, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), "0", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -131);
        insertExercise(4, 3, 3, "wordJSON3", "sentenceJSON3", FINISHED.name(), "0", instance.getTime());

        // when
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(HOUR, -2);
        List<WordRepetitionProgressMapping> result = exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(5, instance.getTime(), FINISHED.name());

        // then
        assertEquals(3, result.size());
        Iterator<WordRepetitionProgressMapping> iterator = result.iterator();
        WordRepetitionProgressMapping current = iterator.next();
        while (iterator.hasNext()) {
            assertEquals(FINISHED.name(), current.getStatus());
            Date olderDate = current.getUpdatedDate();
            current = iterator.next();
            assertTrue(olderDate.after(current.getUpdatedDate()));
        }
    }

    @Test
    public void findFinishedWordSetsSortByUpdatedDate() {
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(HOUR, -1);
        insertExercise(1, 5, 0, "wordJSON0", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(HOUR, -3);
        insertExercise(2, 5, 1, "wordJSON1", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -130);
        insertExercise(3, 5, 2, "wordJSON2", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());
        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(MINUTE, -140);
        insertExercise(4, 5, 3, "wordJSON3", "sentenceJSON1", FINISHED.name(), "1", instance.getTime());

        instance = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        instance.add(HOUR, -2);
        List<WordRepetitionProgressMapping> result = exerciseDao.findWordSetsSortByUpdatedDateAndByStatus(2, instance.getTime(), FINISHED.name());
        assertEquals(2, result.size());
    }

    private void insertExercise(int id, int wordSetId, int wordIndex, String word, String sentence, String status, String current) {
        insertExercise(id, wordSetId, wordIndex, word, sentence, status, current, new Date(), 0, 0);
    }

    private void insertExercise(int id, int wordSetId, int wordIndex, String word, String sentence, String status, String current, Date updatedDate) {
        insertExercise(id, wordSetId, wordIndex, word, sentence, status, current, updatedDate, 0, 0);
    }

    private void insertExercise(int id, int wordSetId, int wordIndex, String word, String sentence, String status, String current, Date updatedDate, int repCount, int forgettingCounter) {
        String sql = format("INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');", WORD_REPETITION_PROGRESS_TABLE,
                ID_FN, WORD_SET_ID_FN, WORD_INDEX_FN, WORD_FN, SENTENCE_IDS_FN, STATUS_FN, CURRENT_FN, UPDATED_DATE_FN, REPETITION_COUNTER_FN, "forgettingCounter",
                id, wordSetId, wordIndex, word, sentence, status, current, updatedDate.getTime(), repCount, forgettingCounter);
        databaseHelper.getWritableDatabase().execSQL(sql);
    }
}