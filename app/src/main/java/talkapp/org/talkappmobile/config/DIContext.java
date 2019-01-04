package talkapp.org.talkappmobile.config;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.activity.BaseActivity;
import talkapp.org.talkappmobile.activity.LoginActivity;
import talkapp.org.talkappmobile.activity.MainActivity;
import talkapp.org.talkappmobile.activity.MainActivityDefaultFragment;
import talkapp.org.talkappmobile.activity.PracticeWordSetActivity;
import talkapp.org.talkappmobile.activity.PracticeWordSetFragment;
import talkapp.org.talkappmobile.activity.PracticeWordSetVocabularyFragment;
import talkapp.org.talkappmobile.activity.TopicsFragment;
import talkapp.org.talkappmobile.activity.WordSetsListFragment;
import talkapp.org.talkappmobile.activity.custom.WordSetListAdapter;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetFirstCycleViewStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.component.impl.GrammarCheckServiceImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderRepetitionStrategy;
import talkapp.org.talkappmobile.component.impl.SentenceProviderStrategy;
import talkapp.org.talkappmobile.module.DatabaseModule;
import talkapp.org.talkappmobile.module.GameplayModule;

@Singleton
@Component(modules = {
        GameplayModule.class,
        DatabaseModule.class
})
public interface DIContext {
    void inject(PracticeWordSetActivity target);

    void inject(LoginActivity target);

    void inject(WordSetListAdapter target);

    void inject(MainActivity mainActivity);

    void inject(WordSetsListFragment wordSetsListFragment);

    void inject(TopicsFragment topicsFragment);

    void inject(PracticeWordSetPresenter target);

    void inject(StudyingPracticeWordSetInteractor target);

    void inject(BaseActivity target);

    void inject(PracticeWordSetFragment target);

    void inject(PracticeWordSetVocabularyPresenter target);

    void inject(PracticeWordSetVocabularyInteractor target);

    void inject(PracticeWordSetVocabularyFragment target);

    void inject(PracticeWordSetViewStrategy target);

    void inject(PracticeWordSetFirstCycleViewStrategy target);

    void inject(SentenceProviderStrategy target);

    void inject(SentenceProviderRepetitionStrategy target);

    void inject(SentenceProviderImpl target);

    void inject(GrammarCheckServiceImpl target);

    void inject(MainActivityDefaultFragment target);
}
