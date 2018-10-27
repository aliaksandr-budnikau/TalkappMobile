package talkapp.org.talkappmobile.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.activity.interactor.AllWordSetsInteractor;
import talkapp.org.talkappmobile.activity.interactor.LoginInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.activity.interactor.PracticeWordSetVocabularyInteractor;
import talkapp.org.talkappmobile.activity.interactor.TopicsFragmentInteractor;
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
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseRepository;
import talkapp.org.talkappmobile.component.database.WordSetExperienceRepository;
import talkapp.org.talkappmobile.component.impl.BackendSentenceProviderStrategy;
import talkapp.org.talkappmobile.component.impl.EqualityScorerImpl;
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
public class GameplayModule {
    public static final String[] ARTICLES = new String[]{"a", "an", "the"};
    public static final String[] LAST_SYMBOLS = new String[]{".", "!", "?"};
    public static final String[] PUNCTUATION_MARKS = new String[]{",", ".", "!", "?"};
    public static final String PLACEHOLDER = "...";

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
    public SentenceProviderRepetitionStrategy provideSentenceProviderRepetitionStrategy(BackendServer server, PracticeWordSetExerciseRepository repository) {
        return new SentenceProviderRepetitionStrategy(server, repository);
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
    public TextUtils provideTextUtils() {
        return new TextUtilsImpl(PLACEHOLDER, ARTICLES, LAST_SYMBOLS, PUNCTUATION_MARKS);
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
        return new WordSetExperienceUtilsImpl();
    }

    @Provides
    @Singleton
    public PracticeWordSetInteractor providePracticeWordSetInteractor(WordsCombinator wordsCombinator, SentenceProvider sentenceProvider, SentenceSelector sentenceSelector, RefereeService refereeService, Logger logger, WordSetExperienceRepository experienceRepository, PracticeWordSetExerciseRepository exerciseRepository, Context context, AudioStuffFactory audioStuffFactory, Speaker speaker) {
        return new PracticeWordSetInteractor(wordsCombinator, sentenceProvider, sentenceSelector, refereeService, logger, experienceRepository, exerciseRepository, context, audioStuffFactory, speaker);
    }

    @Provides
    @Singleton
    public AllWordSetsInteractor provideAllWordSetsInteractor(BackendServer server, WordSetExperienceRepository experienceRepository, PracticeWordSetExerciseRepository exerciseRepository) {
        return new AllWordSetsInteractor(server, experienceRepository, exerciseRepository);
    }

    @Provides
    @Singleton
    public LoginInteractor provideLoginInteractor(Logger logger, BackendServer server, TextUtils textUtils) {
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
    public ViewStrategyFactory provideViewStrategyFactory(TextUtils textUtils, WordSetExperienceUtils experienceUtils) {
        return new ViewStrategyFactoryImpl(textUtils, experienceUtils);
    }
}
