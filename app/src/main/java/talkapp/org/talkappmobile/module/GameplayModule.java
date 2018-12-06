package talkapp.org.talkappmobile.module;

import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.activity.interactor.ExceptionHandlerInteractor;
import talkapp.org.talkappmobile.activity.interactor.LoginInteractor;
import talkapp.org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractor;
import talkapp.org.talkappmobile.activity.interactor.MainActivityInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.ViewStrategyFactory;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.component.impl.BackendSentenceProviderStrategy;
import talkapp.org.talkappmobile.component.impl.EqualityScorerImpl;
import talkapp.org.talkappmobile.component.impl.LoggerImpl;
import talkapp.org.talkappmobile.component.impl.RandomSentenceSelectorImpl;
import talkapp.org.talkappmobile.component.impl.RandomWordsCombinatorImpl;
import talkapp.org.talkappmobile.component.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderRepetitionStrategy;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.component.impl.ViewStrategyFactoryImpl;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
@EBean
public class GameplayModule {

    @Bean(LoggerImpl.class)
    Logger logger;
    @Bean(TextUtilsImpl.class)
    TextUtils textUtils;
    @Bean(WordSetExperienceUtilsImpl.class)
    WordSetExperienceUtils experienceUtils;

    @Provides
    @Singleton
    public SentenceSelector provideSentenceSelector() {
        return new RandomSentenceSelectorImpl();
    }

    @Provides
    @Singleton
    public BackendSentenceProviderStrategy provideBackendSentenceProviderStrategy(BackendServer server) {
        return new BackendSentenceProviderStrategy(server);
    }

    @Provides
    @Singleton
    public SentenceProviderRepetitionStrategy provideSentenceProviderRepetitionStrategy(BackendServer server, PracticeWordSetExerciseService exerciseService) {
        return new SentenceProviderRepetitionStrategy(server, exerciseService);
    }

    @Provides
    @Singleton
    public SentenceProvider provideSentenceProvider(BackendSentenceProviderStrategy backendStrategy, SentenceProviderRepetitionStrategy repetitionStrategy) {
        return new SentenceProviderImpl(backendStrategy, repetitionStrategy);
    }

    @Provides
    @Singleton
    public WordsCombinator provideWordsCombinator() {
        return new RandomWordsCombinatorImpl();
    }

    @Provides
    @Singleton
    public RefereeService provideRefereeService(GrammarCheckService grammarCheckService, EqualityScorer equalityScorer) {
        return new RefereeServiceImpl(grammarCheckService, equalityScorer);
    }

    @Provides
    @Singleton
    public EqualityScorer provideEqualityScorer() {
        return new EqualityScorerImpl();
    }

    @Provides
    @Singleton
    public WordSetExperienceUtils provideWordSetExperienceUtils() {
        return experienceUtils;
    }

    @Provides
    @Singleton
    public StudyingPracticeWordSetInteractor providePracticeWordSetInteractor(WordsCombinator wordsCombinator, SentenceProvider sentenceProvider, SentenceSelector sentenceSelector, RefereeService refereeService, WordSetExperienceService experienceService, PracticeWordSetExerciseService exerciseService, Context context, AudioStuffFactory audioStuffFactory, Speaker speaker) {
        return new StudyingPracticeWordSetInteractor(wordsCombinator, sentenceProvider, sentenceSelector, refereeService, logger, experienceService, exerciseService, context, audioStuffFactory, speaker);
    }

    @Provides
    @Singleton
    public RepetitionPracticeWordSetInteractor provideRepetitionPracticeWordSetInteractor(SentenceProvider sentenceProvider, SentenceSelector sentenceSelector, RefereeService refereeService, PracticeWordSetExerciseService exerciseService, Context context, AudioStuffFactory audioStuffFactory, Speaker speaker) {
        return new RepetitionPracticeWordSetInteractor(sentenceProvider, sentenceSelector, refereeService, logger, exerciseService, context, audioStuffFactory, speaker);
    }

    @Provides
    @Singleton
    public StudyingWordSetsListInteractor provideStudyingWordSetsListInteractor(BackendServer server, WordSetExperienceService experienceService, PracticeWordSetExerciseService exerciseService) {
        return new StudyingWordSetsListInteractor(server, experienceService, exerciseService);
    }

    @Provides
    @Singleton
    public RepetitionWordSetsListInteractor provideRepetitionWordSetsListInteractor(PracticeWordSetExerciseService exerciseService) {
        return new RepetitionWordSetsListInteractor(exerciseService);
    }

    @Provides
    @Singleton
    public LoginInteractor provideLoginInteractor(BackendServer server) {
        return new LoginInteractor(logger, server, textUtils);
    }

    @Provides
    @Singleton
    public PracticeWordSetVocabularyInteractor provideWordTranslationInteractor(BackendServer server, Speaker speaker) {
        return new PracticeWordSetVocabularyInteractor(server, speaker);
    }

    @Provides
    @Singleton
    public TopicsFragmentInteractor provideTopicsFragmentInteractor(BackendServer server) {
        return new TopicsFragmentInteractor(server);
    }

    @Provides
    @Singleton
    public ExceptionHandlerInteractor provideExceptionHandlerInteractor() {
        return new ExceptionHandlerInteractor(logger);
    }

    @Provides
    @Singleton
    public MainActivityInteractor provideMainActivityInteractor(BackendServer server) {
        return new MainActivityInteractor(server);
    }

    @Provides
    @Singleton
    public MainActivityDefaultFragmentInteractor provideMainActivityDefaultFragmentInteractor(PracticeWordSetExerciseService exerciseService) {
        return new MainActivityDefaultFragmentInteractor(exerciseService);
    }

    @Provides
    @Singleton
    public ViewStrategyFactory provideViewStrategyFactory() {
        return new ViewStrategyFactoryImpl(textUtils, experienceUtils);
    }
}
