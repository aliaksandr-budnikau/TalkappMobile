package talkapp.org.talkappmobile.activity;

import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import talkapp.org.talkappmobile.activity.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.MainActivityInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.interactor.StatisticActivityInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StrategySwitcherDecorator;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.UserExperienceDecorator;
import talkapp.org.talkappmobile.activity.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.MainActivityPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetViewStrategy;
import talkapp.org.talkappmobile.activity.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.activity.presenter.StatisticActivityPresenter;
import talkapp.org.talkappmobile.activity.presenter.decorator.ButtonsDisablingDecorator;
import talkapp.org.talkappmobile.activity.presenter.decorator.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.activity.presenter.decorator.PleaseWaitProgressBarDecorator;
import talkapp.org.talkappmobile.activity.view.AddingNewWordSetView;
import talkapp.org.talkappmobile.activity.view.MainActivityView;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetView;
import talkapp.org.talkappmobile.activity.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.activity.view.StatisticActivityView;
import talkapp.org.talkappmobile.service.AudioStuffFactory;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.EqualityScorer;
import talkapp.org.talkappmobile.service.Logger;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.TextUtils;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.service.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.service.impl.LoggerBean;
import talkapp.org.talkappmobile.service.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.service.impl.TextUtilsImpl;

@EBean(scope = EBean.Scope.Singleton)
public class PresenterFactory {
    @Bean(EqualityScorerBean.class)
    EqualityScorer equalityScorer;
    @Bean(TextUtilsImpl.class)
    TextUtils textUtils;
    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(AudioStuffFactoryBean.class)
    AudioStuffFactory audioStuffFactory;
    @RootContext
    Context context;

    public IPracticeWordSetPresenter create(PracticeWordSetView view, Context context, boolean repetitionMode) {
        WordRepetitionProgressService progressService = ServiceFactoryBean.getInstance(context).getWordRepetitionProgressService();
        SentenceService sentenceService = ServiceFactoryBean.getInstance(context).getSentenceService(null);
        RefereeService refereeService = new RefereeServiceImpl(equalityScorer);
        PracticeWordSetViewStrategy viewStrategy = new PracticeWordSetViewStrategy(view);
        CurrentPracticeStateService stateService = ServiceFactoryBean.getInstance(context).getCurrentPracticeStateService();
        StudyingPracticeWordSetInteractor studyingPracticeWordSetInteractor = new StudyingPracticeWordSetInteractor(sentenceService, refereeService, logger, ServiceFactoryBean.getInstance(context).getWordTranslationService(), stateService, progressService, context, ServiceFactoryBean.getInstance(context).getSentenceProvider(), audioStuffFactory);
        StrategySwitcherDecorator strategySwitcherDecorator = new StrategySwitcherDecorator(studyingPracticeWordSetInteractor, progressService, stateService);
        PracticeWordSetInteractor interactor = new UserExperienceDecorator(strategySwitcherDecorator, ServiceFactoryBean.getInstance(context).getUserExpService(), stateService, ServiceFactoryBean.getInstance(context).getWordRepetitionProgressService());
        if (repetitionMode) {
            RepetitionPracticeWordSetInteractor repetitionPracticeWordSetInteractor = new RepetitionPracticeWordSetInteractor(sentenceService, refereeService, logger, progressService, ServiceFactoryBean.getInstance(context).getSentenceProvider(), context, stateService, audioStuffFactory);
            strategySwitcherDecorator = new StrategySwitcherDecorator(repetitionPracticeWordSetInteractor, progressService, stateService);
            interactor = new UserExperienceDecorator(strategySwitcherDecorator, ServiceFactoryBean.getInstance(context).getUserExpService(), stateService, ServiceFactoryBean.getInstance(context).getWordRepetitionProgressService());
        }
        PracticeWordSetPresenter presenter = new PracticeWordSetPresenter(interactor, viewStrategy);

        ButtonsDisablingDecorator disablingDecorator = new ButtonsDisablingDecorator(presenter, view);
        PleaseWaitProgressBarDecorator progressBarDecorator = new PleaseWaitProgressBarDecorator(disablingDecorator, view);
        return progressBarDecorator;
    }

    public PracticeWordSetVocabularyPresenter create(PracticeWordSetVocabularyView view) {
        PracticeWordSetVocabularyInteractor interactor = new PracticeWordSetVocabularyInteractor(ServiceFactoryBean.getInstance(context).getWordSetExperienceRepository(), ServiceFactoryBean.getInstance(context).getWordTranslationService(), ServiceFactoryBean.getInstance(context).getWordRepetitionProgressService(), ServiceFactoryBean.getInstance(context).getCurrentPracticeStateService());
        return new PracticeWordSetVocabularyPresenter(view, interactor);
    }

    public MainActivityPresenter create(MainActivityView view, Context context) {
        MainActivityInteractor interactor = new MainActivityInteractor(ServiceFactoryBean.getInstance(context).getTopicService(), ServiceFactoryBean.getInstance(context).getUserExpService(), context);
        return new MainActivityPresenter(view, interactor);
    }

    public StatisticActivityPresenter create(StatisticActivityView view) {
        StatisticActivityInteractor interactor = new StatisticActivityInteractor(ServiceFactoryBean.getInstance(context).getUserExpService());
        return new StatisticActivityPresenter(view, interactor);
    }

    public AddingNewWordSetPresenter create(AddingNewWordSetView view) {
        ServiceFactory serviceFactory = ServiceFactoryBean.getInstance(context);
        WordSetService wordSetService = serviceFactory.getWordSetExperienceRepository();
        WordTranslationService wordTranslationService = serviceFactory.getWordTranslationService();
        AddingNewWordSetInteractor addingNewWordSetInteractor = new AddingNewWordSetInteractor(wordSetService, wordTranslationService);
        return new AddingNewWordSetPresenter(view, addingNewWordSetInteractor);
    }
}