package talkapp.org.talkappmobile.component.impl;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import talkapp.org.talkappmobile.component.TextUtils;
import talkapp.org.talkappmobile.model.GrammarError;

/**
 * @author Budnikau Aliaksandr
 */
public class TextUtilsImpl implements TextUtils {

    private final Set<String> words;
    private final Set<String> lastSymbols;
    private final Set<String> punctuationMarks;
    private final String placeholder;

    public TextUtilsImpl(String placeholder, String[] words, String[] lastSymbols, String[] punctuationMarks) {
        this.placeholder = placeholder;
        this.words = new HashSet<>(Arrays.asList(words));
        this.lastSymbols = new HashSet<>(Arrays.asList(lastSymbols));
        this.punctuationMarks = new HashSet<>(Arrays.asList(punctuationMarks));
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
    public String buildSpellingGrammarErrorMessage(GrammarError e) {
        String errorMessage = e.getMessage() + " in \"" + e.getBad() + "\".";
        if (e.getSuggestions() == null || e.getSuggestions().isEmpty()) {
            return errorMessage;
        }
        StringBuilder builder = new StringBuilder(errorMessage);
        builder.append(" Try ");
        Iterator<String> iterator = e.getSuggestions().iterator();
        while (iterator.hasNext()) {
            builder.append("\"");
            builder.append(iterator.next());
            builder.append("\"");
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append(".");
        return builder.toString();
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