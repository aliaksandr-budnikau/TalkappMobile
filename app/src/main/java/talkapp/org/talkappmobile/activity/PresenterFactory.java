package talkapp.org.talkappmobile.activity;

import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import talkapp.org.talkappmobile.activity.interactor.MainActivityInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.interactor.StatisticActivityInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StrategySwitcherDecorator;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.presenter.MainActivityPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.activity.presenter.StatisticActivityPresenter;
import talkapp.org.talkappmobile.activity.presenter.decorator.ButtonsDisablingDecorator;
import talkapp.org.talkappmobile.activity.presenter.decorator.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.decorator.PleaseWaitProgressBarDecorator;
import talkapp.org.talkappmobile.activity.view.MainActivityView;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.activity.view.StatisticActivityView;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.BackendServerFactory;
import talkapp.org.talkappmobile.service.EqualityScorer;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.TextUtils;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetExperienceUtils;
import talkapp.org.talkappmobile.service.WordSetService;
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

    public IPracticeWordSetPresenter create(PracticeWordSetView view, Context context, boolean repetitionMode) {
        WordRepetitionProgressService progressService = serviceFactory.getPracticeWordSetExerciseRepository();
        SentenceServiceImpl sentenceService = new SentenceServiceImpl(backendServerFactory.get(), progressService, serviceFactory.getMapper());
        RefereeService refereeService = new RefereeServiceImpl(equalityScorer);
        PracticeWordSetViewStrategy viewStrategy = new PracticeWordSetViewStrategy(view, textUtils, experienceUtils);

        WordSetService wordSetService = serviceFactory.getWordSetExperienceRepository();
        StudyingPracticeWordSetInteractor studyingPracticeWordSetInteractor = new StudyingPracticeWordSetInteractor(wordSetService, sentenceService, refereeService, logger, wordSetService, serviceFactory.getWordTranslationService(), progressService, serviceFactory.getUserExpService(), context, audioStuffFactory);
        PracticeWordSetInteractor interactor = new StrategySwitcherDecorator(studyingPracticeWordSetInteractor, wordSetService, experienceUtils, progressService);
        if (repetitionMode) {
            RepetitionPracticeWordSetInteractor repetitionPracticeWordSetInteractor = new RepetitionPracticeWordSetInteractor(sentenceService, refereeService, logger, progressService, serviceFactory.getUserExpService(), experienceUtils, wordSetService, serviceFactory.getWordTranslationService(), context, audioStuffFactory);
            interactor = new StrategySwitcherDecorator(repetitionPracticeWordSetInteractor, wordSetService, experienceUtils, progressService);
        }
        PracticeWordSetPresenter presenter = new PracticeWordSetPresenter(interactor, viewStrategy);

        ButtonsDisablingDecorator disablingDecorator = new ButtonsDisablingDecorator(presenter, view);
        PleaseWaitProgressBarDecorator progressBarDecorator = new PleaseWaitProgressBarDecorator(disablingDecorator, view);
        return progressBarDecorator;
    }

    public PracticeWordSetVocabularyPresenter create(PracticeWordSetVocabularyView view) {
        PracticeWordSetVocabularyInteractor interactor = new PracticeWordSetVocabularyInteractor(backendServerFactory.get(), serviceFactory.getWordSetExperienceRepository(), serviceFactory.getWordTranslationService(), serviceFactory.getPracticeWordSetExerciseRepository());
        return new PracticeWordSetVocabularyPresenter(view, interactor);
    }

    public MainActivityPresenter create(MainActivityView view, Context context) {
        MainActivityInteractor interactor = new MainActivityInteractor(backendServerFactory.get(), serviceFactory.getUserExpService(), context);
        return new MainActivityPresenter(view, interactor);
    }

    public StatisticActivityPresenter create(StatisticActivityView view) {
        StatisticActivityInteractor interactor = new StatisticActivityInteractor(serviceFactory.getUserExpService());
        return new StatisticActivityPresenter(view, interactor);
    }
}