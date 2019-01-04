package talkapp.org.talkappmobile.module;

import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.activity.interactor.MainActivityDefaultFragmentInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.RepetitionWordSetsListInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingPracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.impl.StudyingWordSetsListInteractor;
import talkapp.org.talkappmobile.component.AudioStuffFactory;
import talkapp.org.talkappmobile.component.EqualityScorer;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.RefereeService;
import talkapp.org.talkappmobile.component.SentenceProvider;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.backend.BackendServerFactory;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.component.database.WordSetExperienceService;
import talkapp.org.talkappmobile.component.impl.AudioStuffFactoryBean;
import talkapp.org.talkappmobile.component.impl.BackendSentenceProviderStrategy;
import talkapp.org.talkappmobile.component.impl.EqualityScorerBean;
import talkapp.org.talkappmobile.component.impl.GrammarCheckServiceImpl;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.component.impl.RandomSentenceSelectorBean;
import talkapp.org.talkappmobile.component.impl.RandomWordsCombinatorBean;
import talkapp.org.talkappmobile.component.impl.RefereeServiceImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderImpl;
import talkapp.org.talkappmobile.component.impl.SentenceProviderRepetitionStrategy;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
@EBean
public class GameplayModule {

    @Bean(LoggerBean.class)
    Logger logger;
    @Bean(TextUtilsImpl.class)
    TextUtils textUtils;
    @Bean(WordSetExperienceUtilsImpl.class)
    WordSetExperienceUtils experienceUtils;
    @Bean(RandomWordsCombinatorBean.class)
    WordsCombinator wordsCombinator;
    @Bean(RandomSentenceSelectorBean.class)
    SentenceSelector sentenceSelector;
    @Bean(AudioStuffFactoryBean.class)
    AudioStuffFactory audioStuffFactory;
    @Bean(EqualityScorerBean.class)
    EqualityScorer equalityScorer;
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;
    @Bean(SpeakerBean.class)
    Speaker speaker;

    @RootContext
    Context context;

    @Provides
    @Singleton
    public SentenceProviderRepetitionStrategy provideSentenceProviderRepetitionStrategy(PracticeWordSetExerciseService exerciseService) {
        return new SentenceProviderRepetitionStrategy(backendServerFactory.get(), exerciseService);
    }

    @Provides
    @Singleton
    public SentenceProvider provideSentenceProvider(SentenceProviderRepetitionStrategy repetitionStrategy) {
        BackendSentenceProviderStrategy backendStrategy = new BackendSentenceProviderStrategy(backendServerFactory.get());
        return new SentenceProviderImpl(backendStrategy, repetitionStrategy);
    }

    @Provides
    @Singleton
    public RefereeService provideRefereeService() {
        GrammarCheckServiceImpl grammarCheckService = new GrammarCheckServiceImpl(backendServerFactory.get(), logger);
        return new RefereeServiceImpl(grammarCheckService, equalityScorer);
    }

    @Provides
    @Singleton
    public WordSetExperienceUtils provideWordSetExperienceUtils() {
        return experienceUtils;
    }

    @Provides
    @Singleton
    public StudyingPracticeWordSetInteractor providePracticeWordSetInteractor(SentenceProvider sentenceProvider, RefereeService refereeService, WordSetExperienceService experienceService, PracticeWordSetExerciseService exerciseService) {
        return new StudyingPracticeWordSetInteractor(wordsCombinator, sentenceProvider, sentenceSelector, refereeService, logger, experienceService, exerciseService, context, audioStuffFactory, speaker);
    }

    @Provides
    @Singleton
    public RepetitionPracticeWordSetInteractor provideRepetitionPracticeWordSetInteractor(SentenceProvider sentenceProvider, RefereeService refereeService, PracticeWordSetExerciseService exerciseService) {
        return new RepetitionPracticeWordSetInteractor(sentenceProvider, sentenceSelector, refereeService, logger, exerciseService, context, audioStuffFactory, speaker);
    }

    @Provides
    @Singleton
    public StudyingWordSetsListInteractor provideStudyingWordSetsListInteractor(WordSetExperienceService experienceService, PracticeWordSetExerciseService exerciseService) {
        return new StudyingWordSetsListInteractor(backendServerFactory.get(), experienceService, exerciseService);
    }

    @Provides
    @Singleton
    public RepetitionWordSetsListInteractor provideRepetitionWordSetsListInteractor(PracticeWordSetExerciseService exerciseService) {
        return new RepetitionWordSetsListInteractor(exerciseService);
    }

    @Provides
    @Singleton
    public MainActivityDefaultFragmentInteractor provideMainActivityDefaultFragmentInteractor(PracticeWordSetExerciseService exerciseService) {
        return new MainActivityDefaultFragmentInteractor(exerciseService);
    }
}
