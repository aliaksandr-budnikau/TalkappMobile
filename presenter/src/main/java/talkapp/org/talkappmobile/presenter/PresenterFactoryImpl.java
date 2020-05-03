package talkapp.org.talkappmobile.presenter;

import android.content.Context;

import javax.inject.Inject;

import talkapp.org.talkappmobile.DaggerPresenterComponent;
import talkapp.org.talkappmobile.PresenterComponent;
import talkapp.org.talkappmobile.PresenterModule;
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
import talkapp.org.talkappmobile.service.TopicService;
import talkapp.org.talkappmobile.service.UserExpService;
import talkapp.org.talkappmobile.service.WordRepetitionProgressService;
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
    @Inject
    CurrentPracticeStateService currentPracticeStateService;
    @Inject
    WordRepetitionProgressService wordRepetitionProgressService;
    @Inject
    UserExpService userExpService;
    @Inject
    TopicService topicService;

    @Inject
    StudyingPracticeWordSetInteractor studyingPracticeWordSetInteractor;
    @Inject
    RepetitionPracticeWordSetInteractor repetitionPracticeWordSetInteractor;
    @Inject
    PracticeWordSetVocabularyInteractor practiceWordSetVocabularyInteractor;
    @Inject
    StatisticActivityInteractor statisticActivityInteractor;
    @Inject
    AddingNewWordSetInteractor addingNewWordSetInteractor;
    @Inject
    TopicsFragmentInteractor topicsFragmentInteractor;
    @Inject
    StudyingWordSetsListInteractor studyingWordSetsListInteractor;
    @Inject
    RightAnswerTextViewInteractor rightAnswerTextViewInteractor;
    @Inject
    ExceptionHandlerInteractor exceptionHandlerInteractor;
    @Inject
    WordSetQRImporterBeanInteractor wordSetQRImporterBeanInteractor;
    @Inject
    WordSetsListItemViewInteractor wordSetsListItemViewInteractor;
    @Inject
    PronounceRightAnswerButtonInteractor pronounceRightAnswerButtonInteractor;

    public PresenterFactoryImpl(Context context) {
        PresenterComponent component = DaggerPresenterComponent.builder()
                .presenterModule(new PresenterModule(context)).build();
        component.inject(this);
    }

    @Override
    public IPracticeWordSetPresenter create(PracticeWordSetView view, boolean repetitionMode) {
        PracticeWordSetViewStrategy viewStrategy = new PracticeWordSetViewStrategy(view);
        StrategySwitcherDecorator strategySwitcherDecorator = new StrategySwitcherDecorator(studyingPracticeWordSetInteractor, wordRepetitionProgressService, currentPracticeStateService);
        PracticeWordSetInteractor interactor = new UserExperienceDecorator(strategySwitcherDecorator, userExpService, currentPracticeStateService, wordRepetitionProgressService);
        if (repetitionMode) {
            strategySwitcherDecorator = new StrategySwitcherDecorator(repetitionPracticeWordSetInteractor, wordRepetitionProgressService, currentPracticeStateService);
            interactor = new UserExperienceDecorator(strategySwitcherDecorator, userExpService, currentPracticeStateService, wordRepetitionProgressService);
        }
        IPracticeWordSetPresenter presenter = new PracticeWordSetPresenterImpl(interactor, viewStrategy);

        ButtonsDisablingDecorator disablingDecorator = new ButtonsDisablingDecorator(presenter, view);
        PleaseWaitProgressBarDecorator progressBarDecorator = new PleaseWaitProgressBarDecorator(disablingDecorator, view);
        return progressBarDecorator;
    }

    @Override
    public PracticeWordSetVocabularyPresenter create(PracticeWordSetVocabularyView view) {
        return new PracticeWordSetVocabularyPresenterImpl(view, practiceWordSetVocabularyInteractor);
    }

    @Override
    public MainActivityPresenter create(MainActivityView view, String versionName) {
        MainActivityInteractor interactor = new MainActivityInteractor(topicService, userExpService, versionName);
        return new MainActivityPresenterImpl(view, interactor);
    }

    @Override
    public StatisticActivityPresenter create(StatisticActivityView view) {
        return new StatisticActivityPresenterImpl(view, statisticActivityInteractor);
    }

    @Override
    public AddingNewWordSetPresenter create(AddingNewWordSetView view) {
        return new AddingNewWordSetPresenterImpl(view, addingNewWordSetInteractor);
    }

    @Override
    public WordSetsListPresenter create(WordSetsListView view, boolean repetitionMode, RepetitionClass repetitionClass, Topic topic) {
        WordSetsListInteractor interactor = studyingWordSetsListInteractor;
        if (repetitionMode) {
            interactor = new RepetitionWordSetsListInteractor(wordRepetitionProgressService, repetitionClass == null ? RepetitionClass.NEW : repetitionClass);
        }
        return new WordSetsListPresenterImpl(topic, view, interactor);
    }

    @Override
    public TopicsFragmentPresenter create(TopicsFragmentView view) {
        return new TopicsFragmentPresenterImpl(view, topicsFragmentInteractor);
    }

    @Override
    public RightAnswerTextViewPresenter create(RightAnswerTextViewView view) {
        return new RightAnswerTextViewPresenterImpl(rightAnswerTextViewInteractor, view);
    }

    @Override
    public MainActivityDefaultFragmentPresenter create(MainActivityDefaultFragmentView view, String wordSetsRepetitionTitle, String wordSetsRepetitionDescription, String wordSetsLearningTitle, String wordSetsLearningDescription, String wordSetsAddNewTitle, String wordSetsAddNewDescription, String wordSetsExtraRepetitionTitle, String wordSetsExtraRepetitionDescription) {
        MainActivityDefaultFragmentInteractor interactor = new MainActivityDefaultFragmentInteractor(wordRepetitionProgressService,
                wordSetsRepetitionTitle, wordSetsRepetitionDescription,
                wordSetsLearningTitle, wordSetsLearningDescription,
                wordSetsAddNewTitle, wordSetsAddNewDescription,
                wordSetsExtraRepetitionTitle, wordSetsExtraRepetitionDescription
        );
        return new MainActivityDefaultFragmentPresenterImpl(view, interactor);
    }

    @Override
    public ExceptionHandlerPresenter create(ExceptionHandlerView view) {
        return new ExceptionHandlerPresenterImpl(view, exceptionHandlerInteractor);
    }

    @Override
    public WordSetQRImporterBeanPresenter create(WordSetQRImporterView view) {
        return new WordSetQRImporterBeanPresenterImpl(view, wordSetQRImporterBeanInteractor);
    }

    @Override
    public WordSetsListItemViewPresenter create(WordSetsListItemViewView view) {
        return new WordSetsListItemViewPresenterImpl(wordSetsListItemViewInteractor, view);
    }

    @Override
    public PronounceRightAnswerButtonPresenter create(PronounceRightAnswerButtonView view) {
        return new PronounceRightAnswerButtonPresenterImpl(pronounceRightAnswerButtonInteractor, view);
    }

    @Override
    public OriginalTextTextViewPresenter create(OriginalTextTextViewView view) {
        return new OriginalTextTextViewPresenterImpl(view);
    }
}