package talkapp.org.talkappmobile.config;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.activity.ExerciseActivity;
import talkapp.org.talkappmobile.activity.WordsSetsListActivity;
import talkapp.org.talkappmobile.activity.adapter.WordSetListAdapter;
import talkapp.org.talkappmobile.bean.AsyncTasksModule;
import talkapp.org.talkappmobile.bean.BackEndServiceModule;
import talkapp.org.talkappmobile.bean.ItemsListModule;
import talkapp.org.talkappmobile.bean.TranslationExerciseModule;

@Singleton
@Component(modules = {
        BackEndServiceModule.class,
        TranslationExerciseModule.class,
        ItemsListModule.class,
        AsyncTasksModule.class
})
public abstract class DIContext {

    private static DIContext instance;

    public static DIContext get() {
        if (instance == null) {
            instance = DaggerDIContext.builder()
                    .translationExerciseModule(new TranslationExerciseModule())
                    .backEndServiceModule(new BackEndServiceModule())
                    .itemsListModule(new ItemsListModule())
                    .asyncTasksModule(new AsyncTasksModule())
                    .build();
        }
        return instance;
    }

    abstract public void inject(ExerciseActivity target);

    abstract public void inject(WordSetListAdapter target);

    abstract public void inject(WordsSetsListActivity target);
}
