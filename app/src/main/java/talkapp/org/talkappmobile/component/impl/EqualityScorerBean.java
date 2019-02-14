package talkapp.org.talkappmobile.component.impl;

import org.androidannotations.annotations.EBean;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import talkapp.org.talkappmobile.component.EqualityScorer;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@EBean(scope = EBean.Scope.Singleton)
public class EqualityScorerBean implements EqualityScorer {

    public static final String REPLACEMENT = " ";
    public static final String REGEX = "[^A-Za-z0-9 ]";

    @Override
    public int score(String expected, String actual) {
        expected = expected.toLowerCase().replaceAll(REGEX, REPLACEMENT)
                .replaceAll(" {1,}", REPLACEMENT).trim();
        actual = actual.toLowerCase().replaceAll(REGEX, REPLACEMENT)
                .replaceAll(" {1,}", REPLACEMENT).trim();
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
        String[] split = sentence.toLowerCase().replaceAll(REGEX, REPLACEMENT).split(REPLACEMENT);
        LinkedList<String> result = new LinkedList<>();
        for (String token : split) {
            token = token.trim();
            if (!isEmpty(token)) {
                result.add(token);
            }
        }
        return new HashSet<>(result);
    }
}
