package talkapp.org.talkappmobile;

import android.content.Context;

import talkapp.org.talkappmobile.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.interactor.MainActivityInteractor;
import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.interactor.StatisticActivityInteractor;
import talkapp.org.talkappmobile.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.interactor.impl.StrategySwitcherDecorator;
import talkapp.org.talkappmobile.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.interactor.impl.UserExperienceDecorator;
import talkapp.org.talkappmobile.presenter.AddingNewWordSetPresenter;
import talkapp.org.talkappmobile.presenter.MainActivityPresenter;
import talkapp.org.talkappmobile.presenter.PracticeWordSetPresenter;
import talkapp.org.talkappmobile.presenter.PracticeWordSetViewStrategy;
import talkapp.org.talkappmobile.presenter.PracticeWordSetVocabularyPresenter;
import talkapp.org.talkappmobile.presenter.StatisticActivityPresenter;
import talkapp.org.talkappmobile.presenter.decorator.ButtonsDisablingDecorator;
import talkapp.org.talkappmobile.presenter.decorator.IPracticeWordSetPresenter;
import talkapp.org.talkappmobile.presenter.decorator.PleaseWaitProgressBarDecorator;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.service.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.view.AddingNewWordSetView;
import talkapp.org.talkappmobile.view.MainActivityView;
import talkapp.org.talkappmobile.view.PracticeWordSetView;
import talkapp.org.talkappmobile.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.view.StatisticActivityView;

public class PresenterFactory {

    public IPracticeWordSetPresenter create(PracticeWordSetView view, Context context, boolean repetitionMode) {
        ServiceFactory serviceFactory = ServiceFactoryBean.getInstance(context);
        WordRepetitionProgressService progressService = serviceFactory.getWordRepetitionProgressService();
        SentenceService sentenceService = serviceFactory.getSentenceService(null);
        RefereeService refereeService = new RefereeServiceImpl(serviceFactory.getEqualityScorer());
        PracticeWordSetViewStrategy viewStrategy = new PracticeWordSetViewStrategy(view);
        CurrentPracticeStateService stateService = serviceFactory.getCurrentPracticeStateService();
        StudyingPracticeWordSetInteractor studyingPracticeWordSetInteractor = new StudyingPracticeWordSetInteractor(sentenceService, refereeService, serviceFactory.getLogger(), serviceFactory.getWordTranslationService(), stateService, progressService, context, serviceFactory.getSentenceProvider(), serviceFactory.getAudioStuffFactory());
        StrategySwitcherDecorator strategySwitcherDecorator = new StrategySwitcherDecorator(studyingPracticeWordSetInteractor, progressService, stateService);
        PracticeWordSetInteractor interactor = new UserExperienceDecorator(strategySwitcherDecorator, serviceFactory.getUserExpService(), stateService, serviceFactory.getWordRepetitionProgressService());
        if (repetitionMode) {
            RepetitionPracticeWordSetInteractor repetitionPracticeWordSetInteractor = new RepetitionPracticeWordSetInteractor(sentenceService, refereeService, serviceFactory.getLogger(), progressService, serviceFactory.getSentenceProvider(), context, stateService, serviceFactory.getAudioStuffFactory());
            strategySwitcherDecorator = new StrategySwitcherDecorator(repetitionPracticeWordSetInteractor, progressService, stateService);
            interactor = new UserExperienceDecorator(strategySwitcherDecorator, serviceFactory.getUserExpService(), stateService, serviceFactory.getWordRepetitionProgressService());
        }
        PracticeWordSetPresenter presenter = new PracticeWordSetPresenter(interactor, viewStrategy);

        ButtonsDisablingDecorator disablingDecorator = new ButtonsDisablingDecorator(presenter, view);
        PleaseWaitProgressBarDecorator progressBarDecorator = new PleaseWaitProgressBarDecorator(disablingDecorator, view);
        return progressBarDecorator;
    }

    public PracticeWordSetVocabularyPresenter create(PracticeWordSetVocabularyView view, Context context) {
        PracticeWordSetVocabularyInteractor interactor = new PracticeWordSetVocabularyInteractor(ServiceFactoryBean.getInstance(context).getWordSetExperienceRepository(), ServiceFactoryBean.getInstance(context).getWordTranslationService(), ServiceFactoryBean.getInstance(context).getWordRepetitionProgressService(), ServiceFactoryBean.getInstance(context).getCurrentPracticeStateService());
        return new PracticeWordSetVocabularyPresenter(view, interactor);
    }

    public MainActivityPresenter create(MainActivityView view, Context context) {
        MainActivityInteractor interactor = new MainActivityInteractor(ServiceFactoryBean.getInstance(context).getTopicService(), ServiceFactoryBean.getInstance(context).getUserExpService(), context);
        return new MainActivityPresenter(view, interactor);
    }

    public StatisticActivityPresenter create(StatisticActivityView view, Context context) {
        StatisticActivityInteractor interactor = new StatisticActivityInteractor(ServiceFactoryBean.getInstance(context).getUserExpService());
        return new StatisticActivityPresenter(view, interactor);
    }

    public AddingNewWordSetPresenter create(AddingNewWordSetView view, Context context) {
        ServiceFactory serviceFactory = ServiceFactoryBean.getInstance(context);
        WordSetService wordSetService = serviceFactory.getWordSetExperienceRepository();
        WordTranslationService wordTranslationService = serviceFactory.getWordTranslationService();
        AddingNewWordSetInteractor addingNewWordSetInteractor = new AddingNewWordSetInteractor(wordSetService, wordTranslationService, serviceFactory.getDataServer());
        return new AddingNewWordSetPresenter(view, addingNewWordSetInteractor);
    }
}