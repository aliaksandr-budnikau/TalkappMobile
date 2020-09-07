package talkapp.org.talkappmobile.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.mappings.NewWordSetDraftMapping;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.mappings.TopicMapping;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.mappings.WordTranslationMapping;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "talkapp.db";
    private static final int DATABASE_VERSION = 52;
    private Map<Integer, List<String>> changes = new LinkedHashMap<>();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        changes.put(37, singletonList(
                "ALTER TABLE WordRepetitionProgress ADD forgettingCounter INTEGER DEFAULT 0 NOT NULL;"
        ));
        changes.put(39, singletonList(
                "CREATE TABLE " + NewWordSetDraftMapping.NEW_WORD_SET_DRAFT_MAPPING_TABLE + " (" + NewWordSetDraftMapping.ID_FN + " INTEGER NOT NULL PRIMARY KEY, " + NewWordSetDraftMapping.WORDS_FN + " VARCHAR NOT NULL);"
        ));
        changes.put(40, singletonList(
                "ALTER TABLE WordRepetitionProgress ADD " + WordRepetitionProgressMapping.WORD_INDEX_FN + " INTEGER DEFAULT 0 NOT NULL;"
        ));
        changes.put(43, Collections.<String>emptyList());
        changes.put(44, Collections.<String>emptyList());
        changes.put(45, asList(
                "BEGIN TRANSACTION;",
                "CREATE TABLE WordTranslation_copy(\n" +
                        "    id VARCHAR NOT NULL PRIMARY KEY,\n" +
                        "    word VARCHAR NOT NULL,\n" +
                        "    language VARCHAR NOT NULL,\n" +
                        "    translation VARCHAR NOT NULL,\n" +
                        "    top INTEGER\n" +
                        ");",
                "INSERT INTO WordTranslation_copy (id, word, language, translation, top) SELECT 'old' || abs(random() % 100000000) as id, word, language, translation, top FROM WordTranslation;",
                "DROP TABLE WordTranslation;",
                "ALTER TABLE WordTranslation_copy RENAME TO WordTranslation;",
                "COMMIT;"
        ));
        changes.put(47, singletonList(
                "CREATE TABLE CurrentWordSet (id VARCHAR NOT NULL PRIMARY KEY, words VARCHAR NOT NULL);"
        ));
        changes.put(48, Collections.<String>emptyList());
        changes.put(49, Collections.<String>emptyList());
        changes.put(51, Collections.<String>emptyList());
        changes.put(52, Collections.<String>emptyList());
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, NewWordSetDraftMapping.class);
            TableUtils.createTable(connectionSource, WordRepetitionProgressMapping.class);
            TableUtils.createTable(connectionSource, WordSetMapping.class);
            TableUtils.createTable(connectionSource, ExpAuditMapping.class);
            TableUtils.createTable(connectionSource, WordTranslationMapping.class);
            TableUtils.createTable(connectionSource, TopicMapping.class);
            TableUtils.createTable(connectionSource, SentenceMapping.class);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer) {
        for (Map.Entry<Integer, List<String>> entry : changes.entrySet()) {
            if (entry.getKey() <= oldVer) {
                continue;
            }
            List<String> sqls = entry.getValue();
            if (sqls == null || sqls.isEmpty()) {
                //migrationService.migrate(entry.getKey());
                continue;
            }
            for (String sql : sqls) {
                db.execSQL(sql);
            }
            //migrationService.migrate(entry.getKey());
        }
    }
}