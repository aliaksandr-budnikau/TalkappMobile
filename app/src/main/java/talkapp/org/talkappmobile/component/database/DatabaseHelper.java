package talkapp.org.talkappmobile.component.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "talkapp.db";
    private static final int DATABASE_VERSION = 25;
    private Map<Integer, List<String>> changes = new LinkedHashMap<>();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        changes.put(15, singletonList(
                "CREATE TABLE WordSet (id VARCHAR NOT NULL PRIMARY KEY, topicId VARCHAR NOT NULL, word VARCHAR NOT NULL);"
        ));
        changes.put(16, asList(
                "CREATE TABLE Topic (id INTEGER NOT NULL PRIMARY KEY, name VARCHAR NOT NULL);",
                "CREATE TABLE Sentence (id VARCHAR NOT NULL PRIMARY KEY, text VARCHAR NOT NULL, translations VARCHAR NOT NULL, tokens VARCHAR NOT NULL, contentScore VARCHAR NOT NULL);"
        ));
        changes.put(17, singletonList(
                "CREATE TABLE WordTranslation (word VARCHAR NOT NULL PRIMARY KEY, language VARCHAR NOT NULL, translation VARCHAR NOT NULL);"
        ));
        changes.put(18, asList(
                "DROP TABLE Sentence;",
                "CREATE TABLE Sentence (id VARCHAR NOT NULL PRIMARY KEY, text VARCHAR NOT NULL, translations VARCHAR NOT NULL, tokens VARCHAR NOT NULL, contentScore VARCHAR);"
        ));
        changes.put(19, asList(
                "DROP TABLE WordSet;",
                "CREATE TABLE WordSet (id VARCHAR NOT NULL PRIMARY KEY, topicId VARCHAR NOT NULL, word VARCHAR NOT NULL, top INTEGER);",
                "DROP TABLE WordTranslation;",
                "CREATE TABLE WordTranslation (word VARCHAR NOT NULL PRIMARY KEY, language VARCHAR NOT NULL, translation VARCHAR NOT NULL, top INTEGER);"
        ));
        changes.put(25, asList(
                "DROP TABLE ExpAudit;",
                "CREATE TABLE ExpAudit (id INTEGER PRIMARY KEY AUTOINCREMENT, date DATE NOT NULL, expScore REAL NOT NULL, activityType VARCHAR NOT NULL);"
        ));
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, PracticeWordSetExerciseMapping.class);
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
                continue;
            }
            for (String sql : sqls) {
                db.execSQL(sql);
            }
        }
    }
}