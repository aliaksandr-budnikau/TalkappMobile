package talkapp.org.talkappmobile.presenter;

import android.content.Context;

import talkapp.org.talkappmobile.interactor.AddingNewWordSetInteractor;
import talkapp.org.talkappmobile.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.interactor.MainActivityDefaultFragmentInteractor;
import talkapp.org.talkappmobile.interactor.MainActivityInteractor;
import talkapp.org.talkappmobile.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.interactor.PronounceRightAnswerButtonInteractor;
import talkapp.org.talkappmobile.interactor.RightAnswerTextViewInteractor;
import talkapp.org.talkappmobile.interactor.StatisticActivityInteractor;
import talkapp.org.talkappmobile.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.interactor.WordSetQRImporterBeanInteractor;
import talkapp.org.talkappmobile.interactor.WordSetsListInteractor;
import talkapp.org.talkappmobile.interactor.WordSetsListItemViewInteractor;
import talkapp.org.talkappmobile.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.interactor.impl.RepetitionWordSetsListInteractor;
import talkapp.org.talkappmobile.interactor.impl.StrategySwitcherDecorator;
import talkapp.org.talkappmobile.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.interactor.impl.UserExperienceDecorator;
import talkapp.org.talkappmobile.model.RepetitionClass;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.presenter.decorator.ButtonsDisablingDecorator;
import talkapp.org.talkappmobile.presenter.decorator.PleaseWaitProgressBarDecorator;
import talkapp.org.talkappmobile.service.CurrentPracticeStateService;
import talkapp.org.talkappmobile.service.RefereeService;
import talkapp.org.talkappmobile.service.SentenceService;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.ServiceFactoryProvider;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
import talkapp.org.talkappmobile.service.WordSetService;
import talkapp.org.talkappmobile.service.WordTranslationService;
import talkapp.org.talkappmobile.view.AddingNewWordSetView;
import talkapp.org.talkappmobile.view.ExceptionHandlerView;
import talkapp.org.talkappmobile.view.MainActivityDefaultFragmentView;
import talkapp.org.talkappmobile.view.MainActivityView;
import talkapp.org.talkappmobile.view.OriginalTextTextViewView;
import talkapp.org.talkappmobile.view.PracticeWordSetView;
import talkapp.org.talkappmobile.view.PracticeWordSetVocabularyView;
import talkapp.org.talkappmobile.view.PronounceRightAnswerButtonView;
import talkapp.org.talkappmobile.view.RightAnswerTextViewView;
import talkapp.org.talkappmobile.view.StatisticActivityView;
import talkapp.org.talkappmobile.view.TopicsFragmentView;
import talkapp.org.talkappmobile.view.WordSetQRImporterView;
import talkapp.org.talkappmobile.view.WordSetsListItemViewView;
import talkapp.org.talkappmobile.view.WordSetsListView;

public class PresenterFactoryImpl implements PresenterFactory {
    private final ServiceFactory serviceFactory;

    public PresenterFactoryImpl(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    public PresenterFactoryImpl(Context context) {
        serviceFactory = ServiceFactoryProvider.get(context);
    }

    @Override
    public IPracticeWordSetPresenter create(PracticeWordSetView view, boolean repetitionMode) {
        WordRepetitionProgressService progressService = serviceFactory.getWordRepetitionProgressService();
        SentenceService sentenceService = serviceFactory.getSentenceService();
        RefereeService refereeService = serviceFactory.getRefereeService();
        PracticeWordSetViewStrategy viewStrategy = new PracticeWordSetViewStrategy(view);
        CurrentPracticeStateService stateService = serviceFactory.getCurrentPracticeStateService();
        StudyingPracticeWordSetInteractor studyingPracticeWordSetInteractor = new StudyingPracticeWordSetInteractor(sentenceService, refereeService, serviceFactory.getLogger(), stateService, progressService, serviceFactory.getSentenceProvider());
        StrategySwitcherDecorator strategySwitcherDecorator = new StrategySwitcherDecorator(studyingPracticeWordSetInteractor, progressService, stateService);
        PracticeWordSetInteractor interactor = new UserExperienceDecorator(strategySwitcherDecorator, serviceFactory.getUserExpService(), stateService, serviceFactory.getWordRepetitionProgressService());
        if (repetitionMode) {
            RepetitionPracticeWordSetInteractor repetitionPracticeWordSetInteractor = new RepetitionPracticeWordSetInteractor(sentenceService, refereeService, serviceFactory.getLogger(), progressService, serviceFactory.getSentenceProvider(), stateService);
            strategySwitcherDecorator = new StrategySwitcherDecorator(repetitionPracticeWordSetInteractor, progressService, stateService);
            interactor = new UserExperienceDecorator(strategySwitcherDecorator, serviceFactory.getUserExpService(), stateService, serviceFactory.getWordRepetitionProgressService());
        }
        IPracticeWordSetPresenter presenter = new PracticeWordSetPresenterImpl(interactor, viewStrategy);

        ButtonsDisablingDecorator disablingDecorator = new ButtonsDisablingDecorator(presenter, view);
        PleaseWaitProgressBarDecorator progressBarDecorator = new PleaseWaitProgressBarDecorator(disablingDecorator, view);
        return progressBarDecorator;
    }

    @Override
    public PracticeWordSetVocabularyPresenter create(PracticeWordSetVocabularyView view) {
        PracticeWordSetVocabularyInteractor interactor = new PracticeWordSetVocabularyInteractor(serviceFactory.getWordSetService(), serviceFactory.getWordTranslationService(), serviceFactory.getWordRepetitionProgressService(), serviceFactory.getCurrentPracticeStateService());
        return new PracticeWordSetVocabularyPresenterImpl(view, interactor);
    }

    @Override
    public MainActivityPresenter create(MainActivityView view, String versionName) {
        MainActivityInteractor interactor = new MainActivityInteractor(serviceFactory.getTopicService(), serviceFactory.getUserExpService(), versionName);
        return new MainActivityPresenterImpl(view, interactor);
    }

    @Override
    public StatisticActivityPresenter create(StatisticActivityView view) {
        StatisticActivityInteractor interactor = new StatisticActivityInteractor(serviceFactory.getUserExpService());
        return new StatisticActivityPresenterImpl(view, interactor);
    }

    @Override
    public AddingNewWordSetPresenter create(AddingNewWordSetView view) {
        WordSetService wordSetService = serviceFactory.getWordSetService();
        WordTranslationService wordTranslationService = serviceFactory.getWordTranslationService();
        AddingNewWordSetInteractor addingNewWordSetInteractor = new AddingNewWordSetInteractor(wordSetService, wordTranslationService, serviceFactory.getDataServer());
        return new AddingNewWordSetPresenterImpl(view, addingNewWordSetInteractor);
    }

    @Override
    public WordSetsListPresenter create(WordSetsListView view, boolean repetitionMode, RepetitionClass repetitionClass, Topic topic) {
        WordSetsListInteractor interactor = new StudyingWordSetsListInteractor(serviceFactory.getWordTranslationService(), serviceFactory.getWordSetService(), serviceFactory.getWordRepetitionProgressService());
        if (repetitionMode) {
            interactor = new RepetitionWordSetsListInteractor(serviceFactory.getWordRepetitionProgressService(), repetitionClass == null ? RepetitionClass.NEW : repetitionClass);
        }
        return new WordSetsListPresenterImpl(topic, view, interactor);
    }

    @Override
    public TopicsFragmentPresenter create(TopicsFragmentView view) {
        TopicsFragmentInteractor interactor = new TopicsFragmentInteractor(serviceFactory.getTopicService());
        return new TopicsFragmentPresenterImpl(view, interactor);
    }

    @Override
    public RightAnswerTextViewPresenter create(RightAnswerTextViewView view) {
        RightAnswerTextViewInteractor interacto = new RightAnswerTextViewInteractor(serviceFactory.getTextUtils());
        return new RightAnswerTextViewPresenterImpl(interacto, view);
    }

    @Override
    public MainActivityDefaultFragmentPresenter create(MainActivityDefaultFragmentView view, String wordSetsRepetitionTitle, String wordSetsRepetitionDescription, String wordSetsLearningTitle, String wordSetsLearningDescription, String wordSetsAddNewTitle, String wordSetsAddNewDescription, String wordSetsExtraRepetitionTitle, String wordSetsExtraRepetitionDescription) {
        MainActivityDefaultFragmentInteractor interactor = new MainActivityDefaultFragmentInteractor(serviceFactory.getWordRepetitionProgressService(),
                wordSetsRepetitionTitle, wordSetsRepetitionDescription,
                wordSetsLearningTitle, wordSetsLearningDescription,
                wordSetsAddNewTitle, wordSetsAddNewDescription,
                wordSetsExtraRepetitionTitle, wordSetsExtraRepetitionDescription
        );
        return new MainActivityDefaultFragmentPresenterImpl(view, interactor);
    }

    @Override
    public ExceptionHandlerPresenter create(ExceptionHandlerView exceptionHandlerView) {
        ExceptionHandlerInteractor interactor = new ExceptionHandlerInteractor(serviceFactory.getLogger());
        return new ExceptionHandlerPresenterImpl(exceptionHandlerView, interactor);
    }

    @Override
    public WordSetQRImporterBeanPresenter create(WordSetQRImporterView view) {
        WordSetService wordSetService = serviceFactory.getWordSetService();
        WordTranslationService wordTranslationService = serviceFactory.getWordTranslationService();
        return new WordSetQRImporterBeanPresenterImpl(view, new WordSetQRImporterBeanInteractor(wordSetService, wordTranslationService));
    }

    @Override
    public WordSetsListItemViewPresenter create(WordSetsListItemViewView view) {
        WordSetsListItemViewInteractor interactor = new WordSetsListItemViewInteractor();
        return new WordSetsListItemViewPresenterImpl(interactor, view);
    }

    @Override
    public PronounceRightAnswerButtonPresenter create(PronounceRightAnswerButtonView view) {
        PronounceRightAnswerButtonInteractor interactor = new PronounceRightAnswerButtonInteractor();
        return new PronounceRightAnswerButtonPresenterImpl(interactor, view);
    }

    @Override
    public OriginalTextTextViewPresenter create(OriginalTextTextViewView view) {
        return new OriginalTextTextViewPresenterImpl(view);
    }
}