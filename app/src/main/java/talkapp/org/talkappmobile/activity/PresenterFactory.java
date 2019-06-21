package talkapp.org.talkappmobile.activity;

import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import talkapp.org.talkappmobile.activity.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.activity.view.AddingNewWordSetFragmentView;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.database.ServiceFactory;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.component.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.component.impl.RandomSentenceSelectorBean;
import talkapp.org.talkappmobile.component.impl.RandomWordsCombinatorBean;
import talkapp.org.talkappmobile.component.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.component.impl.SentenceServiceImpl;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.model.WordSet;

@EBean(scope = EBean.Scope.Singleton)
public class PresenterFactory {
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @Bean(EqualityScorerBean.class)
    EqualityScorer equalityScorer;
    @Bean(TextUtilsImpl.class)
    TextUtils textUtils;
    @Bean(WordSetExperienceUtilsImpl.class)
    WordSetExperienceUtils experienceUtils;
    @Bean(RandomWordsCombinatorBean.class)
    WordsCombinator wordsCombinator;
    @Bean(RandomSentenceSelectorBean.class)
    SentenceSelector sentenceSelector;
    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(AudioStuffFactoryBean.class)
    AudioStuffFactory audioStuffFactory;

    public PracticeWordSetPresenter create(WordSet wordSet, PracticeWordSetView view, Context context, boolean repetitionMode) {
        SentenceServiceImpl sentenceService = new SentenceServiceImpl(backendServerFactory.get(), serviceFactory.getPracticeWordSetExerciseRepository());
        RefereeService refereeService = new RefereeServiceImpl(sentenceService, equalityScorer);
        PracticeWordSetViewStrategy viewStrategy = new PracticeWordSetViewStrategy(view, textUtils, experienceUtils);

        PracticeWordSetInteractor interactor = new StudyingPracticeWordSetInteractor(wordsCombinator, sentenceService, sentenceSelector, refereeService, logger, serviceFactory.getWordSetExperienceRepository(), serviceFactory.getPracticeWordSetExerciseRepository(), serviceFactory.getUserExpService(), experienceUtils, context, audioStuffFactory);
        if (repetitionMode) {
            interactor = new RepetitionPracticeWordSetInteractor(sentenceService, sentenceSelector, refereeService, logger, serviceFactory.getPracticeWordSetExerciseRepository(), serviceFactory.getUserExpService(), serviceFactory.getWordSetExperienceRepository(), experienceUtils, context, audioStuffFactory);
        }
        return new PracticeWordSetPresenter(wordSet, interactor, viewStrategy);
    }

    public AddingNewWordSetPresenter create(AddingNewWordSetFragmentView view) {
        AddingNewWordSetInteractor interactor = new AddingNewWordSetInteractor(backendServerFactory.get());
        return new AddingNewWordSetPresenter(view, interactor);
    }

    public PracticeWordSetVocabularyPresenter create(WordSet wordSet, PracticeWordSetVocabularyView view) {
        PracticeWordSetVocabularyInteractor interactor = new PracticeWordSetVocabularyInteractor(backendServerFactory.get());
        return new PracticeWordSetVocabularyPresenter(wordSet, view, interactor);
    }
}