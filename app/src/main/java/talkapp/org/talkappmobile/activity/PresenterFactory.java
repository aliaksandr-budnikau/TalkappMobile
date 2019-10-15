package talkapp.org.talkappmobile.activity;

import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import talkapp.org.talkappmobile.activity.interactor.MainActivityInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.presenter.MainActivityPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.activity.view.MainActivityView;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.BackendServerFactory;
import talkapp.org.talkappmobile.service.EqualityScorer;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.TextUtils;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.service.impl.SentenceServiceImpl;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.service.impl.WordSetExperienceUtilsImpl;

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
    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(AudioStuffFactoryBean.class)
    AudioStuffFactory audioStuffFactory;

    public PracticeWordSetPresenter create(PracticeWordSetView view, Context context, boolean repetitionMode) {
        SentenceServiceImpl sentenceService = new SentenceServiceImpl(backendServerFactory.get(), serviceFactory.getPracticeWordSetExerciseRepository());
        RefereeService refereeService = new RefereeServiceImpl(equalityScorer);
        PracticeWordSetViewStrategy viewStrategy = new PracticeWordSetViewStrategy(view, textUtils, experienceUtils);

        PracticeWordSetInteractor interactor = new StudyingPracticeWordSetInteractor(serviceFactory.getWordSetExperienceRepository(), sentenceService, refereeService, logger, serviceFactory.getWordSetExperienceRepository(), serviceFactory.getPracticeWordSetExerciseRepository(), serviceFactory.getUserExpService(), experienceUtils, context, audioStuffFactory);
        if (repetitionMode) {
            interactor = new RepetitionPracticeWordSetInteractor(sentenceService, refereeService, logger, serviceFactory.getPracticeWordSetExerciseRepository(), serviceFactory.getUserExpService(), experienceUtils, serviceFactory.getWordSetExperienceRepository(), context, audioStuffFactory);
        }
        return new PracticeWordSetPresenter(interactor, viewStrategy);
    }

    public PracticeWordSetVocabularyPresenter create(WordSet wordSet, PracticeWordSetVocabularyView view) {
        PracticeWordSetVocabularyInteractor interactor = new PracticeWordSetVocabularyInteractor(backendServerFactory.get());
        return new PracticeWordSetVocabularyPresenter(wordSet, view, interactor);
    }

    public MainActivityPresenter create(MainActivityView view, Context context) {
        MainActivityInteractor interactor = new MainActivityInteractor(backendServerFactory.get(), serviceFactory.getUserExpService(), context);
        return new MainActivityPresenter(view, interactor);
    }
}