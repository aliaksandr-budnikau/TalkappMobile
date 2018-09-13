package talkapp.org.talkappmobile.component.impl;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import talkapp.org.talkappmobile.component.TextUtils;

/**
 * @author Budnikau Aliaksandr
 */
public class TextUtilsImpl implements TextUtils {

    private final Set<String> words;
    private final Set<String> lastSymbols;
    private String placeholder;

    public TextUtilsImpl(String placeholder, String[] words, String[] lastSymbols) {
        this.placeholder = placeholder;
        this.words = new HashSet<>(Arrays.asList(words));
        this.lastSymbols = new HashSet<>(Arrays.asList(lastSymbols));
    }

    @Override
    public String screenTextWith(String text) {
        String[] tokens = text.split(" ");
        StringBuilder screened = new StringBuilder();
        for (String token : tokens) {
            if (words.contains(token.toLowerCase())) {
                screened.append(token);
            } else {
                screened.append(placeholder);
            }
            screened.append(" ");
        }
        return screened.toString().trim();
    }

    @Override
    public String hideText(String password) {
        return StringUtils.isEmpty(password) ? "" : "***";
    }

    @Override
    public String toUpperCaseFirstLetter(String text) {
        StringBuilder stringBuilder = new StringBuilder(text);
        stringBuilder.replace(0, 1, text.substring(0, 1).toUpperCase());
        return stringBuilder.toString();
    }

    @Override
    public String appendLastSymbol(String text, String translation) {
        String lastSymbol = text.substring(text.length() - 1);
        if (lastSymbols.contains(lastSymbol)) {
            return text;
        }
        return text + translation.substring(translation.length() - 1);
    }
}