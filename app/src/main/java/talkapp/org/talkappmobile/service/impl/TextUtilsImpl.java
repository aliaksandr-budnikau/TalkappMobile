package talkapp.org.talkappmobile.service.impl;

import org.androidannotations.annotations.EBean;
import org.apache.commons.lang3.StringUtils;
import talkapp.org.talkappmobile.service.TextUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * @author Budnikau Aliaksandr
 */
@EBean
public class TextUtilsImpl implements TextUtils {

    public static final String[] WORDS = new String[]{"a", "an", "the", "um", "uh", "hey", "yeah",
            "huh", "ah", "aha", "ahem", "aw", "aye", "blah", "eh", "eh", "er", "erm", "ew", "ha",
            "ha-ha", "hee", "hm", "hmph", "ho", "huh", "mm-hmm", "mm", "mmm", "oh", "oh-oh", "ooh", "oops",
            "ouch", "shh", "ugh", "uh-huh", "uh-oh", "uh-uh", "whee", "whew", "whoa", "wow", "yay", "yo",
            "yoo", "hoo"};
    public static final String[] LAST_SYMBOLS = new String[]{".", "!", "?"};
    public static final String[] PUNCTUATION_MARKS = new String[]{",", ".", "!", "?"};
    public static final String PLACEHOLDER = "***";

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_PASSWORD_ADDRESS_REGEX = Pattern.compile(
            "^" + // start-of-string
                    "(?=.*[0-9])" + // a digit must occur at least once
                    "(?=.*[a-z])" + // a lower case letter must occur at least once
                    "(?=.*[A-Z])" + // an upper case letter must occur at least once
                    ".{8,}" + // anything, at least eight places though
                    "$" // end-of-string
    );

    private final Set<String> words;
    private final Set<String> lastSymbols;
    private final Set<String> punctuationMarks;
    private final String placeholder;

    public TextUtilsImpl() {
        this.placeholder = PLACEHOLDER;
        this.words = new HashSet<>(asList(WORDS));
        this.lastSymbols = new HashSet<>(asList(LAST_SYMBOLS));
        this.punctuationMarks = new HashSet<>(asList(PUNCTUATION_MARKS));
    }

    public boolean validateEmail(String email) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(email).find();
    }

    public boolean validatePassword(String password) {
        return VALID_PASSWORD_ADDRESS_REGEX.matcher(password).find();
    }

    @Override
    public String screenTextWith(String text) {
        String[] tokens = text.split(" ");
        StringBuilder screened = new StringBuilder();
        for (String token : tokens) {
            String[] wordAndMark = new String[]{token, ""};
            for (String mark : punctuationMarks) {
                if (!token.contains(mark)) {
                    continue;
                }
                int markIndex = token.indexOf(mark);
                if (markIndex > 0) {
                    wordAndMark[0] = token.substring(0, markIndex);
                    wordAndMark[1] = mark;
                    break;
                }
            }
            if (words.contains(wordAndMark[0].toLowerCase())) {
                screened.append(wordAndMark[0]);
            } else {
                screened.append(placeholder);
            }
            screened.append(wordAndMark[1]);
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

    @Override
    public String hideIntervalsInText(String text, Collection<Integer> intervalsToHide) {
        Queue<Integer> queue = new LinkedList<>(intervalsToHide);
        StringBuilder builder = new StringBuilder(text);
        while (!queue.isEmpty()) {
            int start = queue.poll();
            int end = queue.poll();
            builder.replace(start, end, placeholder);
            int shift = end - start - placeholder.length();
            LinkedList<Integer> shiftedQueue = new LinkedList<>();
            for (Integer index : queue) {
                shiftedQueue.addLast(index - shift);
            }
            queue = shiftedQueue;
        }
        return builder.toString();
    }
}