package talkapp.org.talkappmobile.component;

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
}