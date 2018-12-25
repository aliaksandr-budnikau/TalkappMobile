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
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "talkapp.db";
    private static final int DATABASE_VERSION = 13;
    private Map<Integer, List<String>> changes = new LinkedHashMap<>();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        /*changes.put(14, asList(
                "",
                ""
        ));*/
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, PracticeWordSetExerciseMapping.class);
            TableUtils.createTable(connectionSource, WordSetExperienceMapping.class);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer) {
        for (int i = oldVer + 1; i < changes.size(); i++) {
            List<String> sqls = changes.get(i);
            if (sqls == null || sqls.isEmpty()) {
                continue;
            }
            for (String sql : sqls) {
                db.execSQL(sql);
            }
        }
    }
}