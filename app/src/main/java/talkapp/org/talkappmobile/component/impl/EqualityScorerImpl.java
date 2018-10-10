package talkapp.org.talkappmobile.component.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import talkapp.org.talkappmobile.component.EqualityScorer;

public class EqualityScorerImpl implements EqualityScorer {

    public static final String REPLACEMENT = " ";
    public static final String REGEX = "[^A-Za-z0-9]";

    @Override
    public int score(String expected, String actual) {
        if (expected.equals(actual)) {
            return 100;
        }

        Set<String> expectedWords = toSet(expected);
        Set<String> actualWords = toSet(actual);

        int result = 0;
        int unit;
        if (expectedWords.size() >= actualWords.size()) {
            unit = 100 / expectedWords.size();
        } else {
            unit = 100 / (expectedWords.size() + Math.abs(actualWords.size() - expectedWords.size()));
        }
        for (String word : expectedWords) {
            if (actualWords.remove(word)) {
                result += unit;
            }
        }

        return result;
    }

    private HashSet<String> toSet(String sentence) {
        return new HashSet<>(Arrays.asList(sentence.toLowerCase().replaceAll(REGEX, REPLACEMENT).split(REPLACEMENT)));
    }
}
