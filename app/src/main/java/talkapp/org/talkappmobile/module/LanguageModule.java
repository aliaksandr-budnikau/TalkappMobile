package talkapp.org.talkappmobile.module;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import talkapp.org.talkappmobile.component.GrammarCheckService;
import talkapp.org.talkappmobile.component.Logger;
import talkapp.org.talkappmobile.component.impl.GrammarCheckServiceImpl;

/**
 * @author Budnikau Aliaksandr
 */
@Module
public class LanguageModule {

    @Provides
    @Singleton
    public JLanguageTool provideJLanguageTool(AmericanEnglish americanEnglish) {
        return new JLanguageTool(americanEnglish);
    }

    @Provides
    @Singleton
    public AmericanEnglish provideAmericanEnglish() {
        return new AmericanEnglish();
    }

    @Provides
    @Singleton
    public GrammarCheckService provideGrammarCheckService(JLanguageTool languageTool, Logger logger) {
        return new GrammarCheckServiceImpl(languageTool, logger);
    }
}