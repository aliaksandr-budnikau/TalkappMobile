package talkapp.org.talkappmobile.service;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.EqualityScorer;

public class EqualityScorerImpl implements EqualityScorer {

    public static final String REPLACEMENT = " ";
    public static final String REGEX = "[^A-Za-z0-9 ]";

    @Override
    public int score(String expected, String actual, Word2Tokens currentWord) {
        expected = expected.toLowerCase().replaceAll(REGEX, REPLACEMENT)
                .replaceAll(" {1,}", REPLACEMENT).trim();
        actual = actual.toLowerCase().replaceAll(REGEX, REPLACEMENT)
                .replaceAll(" {1,}", REPLACEMENT).trim();
        if (expected.equals(actual)) {
            return 100;
        }

        Set<String> expectedWords = toSet(expected);
        Set<String> actualWords = toSet(actual);

        Boolean contains = checkPresenceOfCurrentWord(currentWord, expectedWords, actualWords);
        if (contains != null && !contains) {
            return 0;
        }

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

    @Nullable
    private Boolean checkPresenceOfCurrentWord(Word2Tokens currentWord, Set<String> expectedWords, Set<String> actualWords) {
        String[] tokens = currentWord.getTokens().split(",");
        for (String token : tokens) {
            for (String expectedWord : expectedWords) {
                if (expectedWord.contains(token)) {
                    for (String actualWord : actualWords) {
                        if (actualWord.contains(token)) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        }
        return null;
    }

    private HashSet<String> toSet(String sentence) {
        String[] split = sentence.toLowerCase().replaceAll(REGEX, REPLACEMENT).split(REPLACEMENT);
        LinkedList<String> result = new LinkedList<>();
        for (String token : split) {
            token = token.trim();
            if (!StringUtils.isEmpty(token)) {
                result.add(token);
            }
        }
        return new HashSet<>(result);
    }
}
