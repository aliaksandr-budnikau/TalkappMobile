package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.activity.PracticeWordSetInteractor;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.impl.RandomSentenceSelectorImpl;
import talkapp.org.talkappmobile.component.impl.RandomWordsCombinatorImpl;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;
import talkapp.org.talkappmobile.config.DIContext;

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
    public WordSetExperienceUtils provideWordSetExperienceUtils() {
        return new WordSetExperienceUtilsImpl();
    }

    @Provides
    @Singleton
    public PracticeWordSetInteractor providePracticeWordSetInteractor() {
        PracticeWordSetInteractor interactor = new PracticeWordSetInteractor();
        DIContext.get().inject(interactor);
        return interactor;
    }
}
