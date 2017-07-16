package talkapp.org.talkappmobile.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import talkapp.org.talkappmobile.service.TextUtils;

/**
 * @author Budnikau Aliaksandr
 */
public class TextUtilsImpl implements TextUtils {

    private final String[] words;
    private String placeholder;

    public TextUtilsImpl(String placeholder, String... words) {
        this.placeholder = placeholder;
        this.words = words;
    }

    @Override
    public String screenTextWith(String text) {
        Set<String> wordsAsSet = new HashSet<>(Arrays.asList(words));

        String[] tokens = text.split(" ");
        StringBuilder screened = new StringBuilder();
        for (String token : tokens) {
            if (wordsAsSet.contains(token.toLowerCase())) {
                screened.append(token);
            } else {
                screened.append(placeholder);
            }
            screened.append(" ");
        }
        return screened.toString().trim();
    }
}