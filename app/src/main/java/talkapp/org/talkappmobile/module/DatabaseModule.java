package talkapp.org.talkappmobile.module;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.db.DatabaseHelper;
import talkapp.org.talkappmobile.db.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.db.dao.impl.PracticeWordSetExerciseDaoImpl;
import talkapp.org.talkappmobile.db.mappings.PracticeWordSetExercise;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public DatabaseHelper provideDatabaseHelper(Context context) {
        return OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    @Provides
    @Singleton
    public PracticeWordSetExerciseDao providePracticeWordSetExerciseDao(DatabaseHelper databaseHelper) {
        try {
            return new PracticeWordSetExerciseDaoImpl(databaseHelper.getConnectionSource(), PracticeWordSetExercise.class);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}