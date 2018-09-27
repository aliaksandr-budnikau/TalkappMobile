package talkapp.org.talkappmobile.config;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.activity.AllWordSetsFragment;
import talkapp.org.talkappmobile.activity.BaseActivity;
import talkapp.org.talkappmobile.activity.LoginActivity;
import talkapp.org.talkappmobile.activity.MainActivity;
import talkapp.org.talkappmobile.activity.PracticeWordSetActivity;
import talkapp.org.talkappmobile.activity.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.PracticeWordSetVocabularyFragment;
import talkapp.org.talkappmobile.activity.TopicsFragment;
import talkapp.org.talkappmobile.activity.WordSetPracticeFragment;
import talkapp.org.talkappmobile.activity.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.activity.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.adapter.TopicListAdapter;
import talkapp.org.talkappmobile.activity.adapter.WordSetListAdapter;
import talkapp.org.talkappmobile.component.impl.RecordedTrackImpl;
import talkapp.org.talkappmobile.module.AudioModule;
import talkapp.org.talkappmobile.module.BackEndServiceModule;
import talkapp.org.talkappmobile.module.ConcurrentModule;
import talkapp.org.talkappmobile.module.DataModule;
import talkapp.org.talkappmobile.module.GameplayModule;
import talkapp.org.talkappmobile.module.InfraModule;
import talkapp.org.talkappmobile.module.ItemsListModule;

@Singleton
@Component(modules = {
        BackEndServiceModule.class,
        GameplayModule.class,
        ConcurrentModule.class,
        AudioModule.class,
        DataModule.class,
        InfraModule.class,
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
                    .infraModule(new InfraModule())
                    .backEndServiceModule(new BackEndServiceModule())
                    .itemsListModule(new ItemsListModule())
                    .build();
        }
        return instance;
    }

    abstract public void inject(PracticeWordSetActivity target);

    abstract public void inject(LoginActivity target);

    abstract public void inject(WordSetListAdapter target);

    abstract public void inject(TopicListAdapter target);

    abstract public void inject(RecordedTrackImpl target);

    abstract public void inject(MainActivity mainActivity);

    public abstract void inject(AllWordSetsFragment allWordSetsFragment);

    public abstract void inject(TopicsFragment topicsFragment);

    public abstract void inject(PracticeWordSetPresenter target);

    public abstract void inject(PracticeWordSetInteractor target);

    public abstract void inject(BaseActivity target);

    public abstract void inject(WordSetPracticeFragment target);

    public abstract void inject(PracticeWordSetVocabularyPresenter target);

    public abstract void inject(PracticeWordSetVocabularyInteractor target);

    public abstract void inject(PracticeWordSetVocabularyFragment target);
}
