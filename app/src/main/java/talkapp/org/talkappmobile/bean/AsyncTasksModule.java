package talkapp.org.talkappmobile.bean;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.activity.adapter.GetWordSetListAsyncTask;
import talkapp.org.talkappmobile.service.WordSetService;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class AsyncTasksModule {

    @Provides
    @Singleton
    public GetWordSetListAsyncTask provideGetWordSetListAsyncTask(WordSetService wordSetService) {
        return new GetWordSetListAsyncTask(wordSetService);
    }
}