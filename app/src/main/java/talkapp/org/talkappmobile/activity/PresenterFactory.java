package talkapp.org.talkappmobile.activity;

import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.talkappmobile.model.WordSet;
import org.talkappmobile.service.AudioStuffFactory;
import org.talkappmobile.service.BackendServerFactory;
import org.talkappmobile.service.EqualityScorer;
import org.talkappmobile.service.Logger;
import org.talkappmobile.service.RefereeService;
import org.talkappmobile.service.ServiceFactory;
import org.talkappmobile.service.TextUtils;
import org.talkappmobile.service.WordSetExperienceUtils;
import org.talkappmobile.service.WordsCombinator;
import org.talkappmobile.service.impl.AudioStuffFactoryBean;
import org.talkappmobile.service.impl.BackendServerFactoryBean;
import org.talkappmobile.service.impl.EqualityScorerBean;
import org.talkappmobile.service.impl.LoggerBean;
import org.talkappmobile.service.impl.RandomWordsCombinatorBean;
import org.talkappmobile.service.impl.RefereeServiceImpl;
import org.talkappmobile.service.impl.SentenceServiceImpl;
import org.talkappmobile.service.impl.ServiceFactoryBean;
import org.talkappmobile.service.impl.TextUtilsImpl;
import org.talkappmobile.service.impl.WordSetExperienceUtilsImpl;

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
    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(AudioStuffFactoryBean.class)
    AudioStuffFactory audioStuffFactory;

    public PracticeWordSetPresenter create(WordSet wordSet, PracticeWordSetView view, Context context, boolean repetitionMode) {
        SentenceServiceImpl sentenceService = new SentenceServiceImpl(backendServerFactory.get(), serviceFactory.getPracticeWordSetExerciseRepository());
        RefereeService refereeService = new RefereeServiceImpl(equalityScorer);
        PracticeWordSetViewStrategy viewStrategy = new PracticeWordSetViewStrategy(view, textUtils, experienceUtils);

        PracticeWordSetInteractor interactor = new StudyingPracticeWordSetInteractor(wordsCombinator, sentenceService, refereeService, logger, serviceFactory.getWordSetExperienceRepository(), serviceFactory.getPracticeWordSetExerciseRepository(), serviceFactory.getUserExpService(), experienceUtils, context, audioStuffFactory);
        if (repetitionMode) {
            interactor = new RepetitionPracticeWordSetInteractor(sentenceService, refereeService, logger, serviceFactory.getPracticeWordSetExerciseRepository(), serviceFactory.getUserExpService(), experienceUtils, wordsCombinator, context, audioStuffFactory);
        }
        return new PracticeWordSetPresenter(interactor, viewStrategy);
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