package talkapp.org.talkappmobile.component;

import java.util.Collection;

import talkapp.org.talkappmobile.model.GrammarError;

/**
 * @author Budnikau Aliaksandr
 */
public interface TextUtils {

    String screenTextWith(String text);

    String hideText(String password);

    String toUpperCaseFirstLetter(String text);

    String appendLastSymbol(String text, String translation);

    String buildSpellingGrammarErrorMessage(GrammarError e);

    String hideIntervalsInText(String text, Collection<Integer> intervalsToHide);
}