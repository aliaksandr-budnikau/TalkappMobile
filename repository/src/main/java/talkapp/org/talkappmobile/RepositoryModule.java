package talkapp.org.talkappmobile;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.dao.DatabaseHelper;

@Module
public class RepositoryModule {

    private final Context context;

    public RepositoryModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public Context context() {
        return context;
    }

    @Provides
    @Singleton
    public DatabaseHelper databaseHelper(Context context) {
        return OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    @Provides
    @Singleton
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }
}

