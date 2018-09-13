package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.GameProcessesFactory;
import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.component.WordSetExperienceUtils;
import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.component.impl.GameProcessesFactoryImpl;
import talkapp.org.talkappmobile.component.impl.RandomSentenceSelectorImpl;
import talkapp.org.talkappmobile.component.impl.RandomWordsCombinatorImpl;
import talkapp.org.talkappmobile.component.impl.TextUtilsImpl;
import talkapp.org.talkappmobile.component.impl.WordSetExperienceUtilsImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class GameplayModule {
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
        String[] articles = new String[]{"a", "an", "the"};
        String[] lastSymbols = new String[]{".", "!", "?"};
        return new TextUtilsImpl("...", articles, lastSymbols);
    }

    @Provides
    @Singleton
    public WordSetExperienceUtils provideWordSetExperienceUtils() {
        return new WordSetExperienceUtilsImpl();
    }

    @Provides
    @Singleton
    public GameProcessesFactory provideGameProcessesFactory() {
        return new GameProcessesFactoryImpl();
    }
}
