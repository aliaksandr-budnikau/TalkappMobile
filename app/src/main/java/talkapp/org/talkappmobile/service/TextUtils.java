package talkapp.org.talkappmobile.service;

/**
 * @author Budnikau Aliaksandr
 */
public interface TextUtils {

    String screenTextWith(String text);

    String toUpperCaseFirstLetter(String text);

    String appendLastSymbol(String text, String translation);
}