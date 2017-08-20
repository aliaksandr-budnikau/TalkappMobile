package talkapp.org.talkappmobile.config;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.activity.AllWordSetsFragment;
import talkapp.org.talkappmobile.activity.LoginActivity;
import talkapp.org.talkappmobile.activity.MainActivity;
import talkapp.org.talkappmobile.activity.PracticeWordSetActivity;
import talkapp.org.talkappmobile.activity.adapter.WordSetListAdapter;
import talkapp.org.talkappmobile.module.AudioModule;
import talkapp.org.talkappmobile.module.BackEndServiceModule;
import talkapp.org.talkappmobile.module.ConcurrentModule;
import talkapp.org.talkappmobile.module.DataModule;
import talkapp.org.talkappmobile.module.GameplayModule;
import talkapp.org.talkappmobile.module.ItemsListModule;
import talkapp.org.talkappmobile.service.impl.RecordedTrackImpl;

@Singleton
@Component(modules = {
        BackEndServiceModule.class,
        GameplayModule.class,
        ConcurrentModule.class,
        AudioModule.class,
        DataModule.class,
        ItemsListModule.class
})
public abstract class DIContext {

    private static DIContext instance;

    public static DIContext get() {
        if (instance == null) {
            instance = DaggerDIContext.builder()
                    .gameplayModule(new GameplayModule())
                    .concurrentModule(new ConcurrentModule())
                    .audioModule(new AudioModule())
                    .dataModule(new DataModule())
                    .backEndServiceModule(new BackEndServiceModule())
                    .itemsListModule(new ItemsListModule())
                    .build();
        }
        return instance;
    }

    abstract public void inject(PracticeWordSetActivity target);

    abstract public void inject(LoginActivity target);

    abstract public void inject(WordSetListAdapter target);

    abstract public void inject(RecordedTrackImpl target);

    abstract public void inject(MainActivity mainActivity);

    public abstract void inject(AllWordSetsFragment allWordSetsFragment);
}
