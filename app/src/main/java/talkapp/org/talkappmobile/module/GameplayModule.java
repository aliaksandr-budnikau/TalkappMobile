package talkapp.org.talkappmobile.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.service.GameProcessesFactory;
import talkapp.org.talkappmobile.service.SentenceSelector;
import talkapp.org.talkappmobile.service.WordsCombinator;
import talkapp.org.talkappmobile.service.impl.GameProcessesFactoryImpl;
import talkapp.org.talkappmobile.service.impl.RandomSentenceSelectorImpl;
import talkapp.org.talkappmobile.service.impl.RandomWordsCombinatorImpl;

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
    public GameProcessesFactory provideGameProcessesFactory(WordsCombinator wordsCombinator,
                                                            SentenceSelector sentenceSelector) {
        return new GameProcessesFactoryImpl(wordsCombinator, sentenceSelector);
    }
}
