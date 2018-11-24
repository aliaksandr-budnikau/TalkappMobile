package talkapp.org.talkappmobile.config;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.activity.AllWordSetsFragment;
import talkapp.org.talkappmobile.activity.BaseActivity;
import talkapp.org.talkappmobile.activity.LoginActivity;
import talkapp.org.talkappmobile.activity.MainActivity;
import talkapp.org.talkappmobile.activity.PracticeWordSetActivity;
import talkapp.org.talkappmobile.activity.PracticeWordSetFragment;
import talkapp.org.talkappmobile.activity.PracticeWordSetVocabularyFragment;
import talkapp.org.talkappmobile.activity.TopicsFragment;
import talkapp.org.talkappmobile.activity.adapter.TopicListAdapter;
import talkapp.org.talkappmobile.activity.adapter.WordSetListAdapter;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewHideNewWordOnlyStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.component.impl.GrammarCheckServiceImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderRepetitionStrategy;
import talkapp.org.talkappmobile.component.impl.SentenceProviderStrategy;
import talkapp.org.talkappmobile.module.AndroidModule;
import talkapp.org.talkappmobile.module.AudioModule;
import talkapp.org.talkappmobile.module.BackEndServiceModule;
import talkapp.org.talkappmobile.module.DataModule;
import talkapp.org.talkappmobile.module.DatabaseModule;
import talkapp.org.talkappmobile.module.GameplayModule;
import talkapp.org.talkappmobile.module.InfraModule;
import talkapp.org.talkappmobile.module.ItemsListModule;
import talkapp.org.talkappmobile.module.LanguageModule;
import talkapp.org.talkappmobile.module.ViewModule;

@Singleton
@Component(modules = {
        BackEndServiceModule.class,
        GameplayModule.class,
        AndroidModule.class,
        DataModule.class,
        AudioModule.class,
        InfraModule.class,
        LanguageModule.class,
        ViewModule.class,
        DatabaseModule.class,
        ItemsListModule.class
})
public interface DIContext {
    void inject(PracticeWordSetActivity target);

    void inject(LoginActivity target);

    void inject(WordSetListAdapter target);

    void inject(TopicListAdapter target);

    void inject(MainActivity mainActivity);

    void inject(AllWordSetsFragment allWordSetsFragment);

    void inject(TopicsFragment topicsFragment);

    void inject(PracticeWordSetPresenter target);

    void inject(PracticeWordSetInteractor target);

    void inject(BaseActivity target);

    void inject(PracticeWordSetFragment target);

    void inject(PracticeWordSetVocabularyPresenter target);

    void inject(PracticeWordSetVocabularyInteractor target);

    void inject(PracticeWordSetVocabularyFragment target);

    void inject(PracticeWordSetViewStrategy target);

    void inject(PracticeWordSetViewHideNewWordOnlyStrategy target);

    void inject(SentenceProviderStrategy target);

    void inject(SentenceProviderRepetitionStrategy target);

    void inject(SentenceProviderImpl target);

    void inject(GrammarCheckServiceImpl target);
}
