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

import talkapp.org.talkappmobile.component.database.mappings.ExpAuditMapping;
import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.TopicMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.WordTranslationMapping;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "talkapp.db";
    private static final int DATABASE_VERSION = 35;
    private Map<Integer, List<String>> changes = new LinkedHashMap<>();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
/*        changes.put(35, singletonList(
                "SOME SQL;"
        ));*/
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
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
                continue;
            }
            for (String sql : sqls) {
                db.execSQL(sql);
            }
        }
    }
}