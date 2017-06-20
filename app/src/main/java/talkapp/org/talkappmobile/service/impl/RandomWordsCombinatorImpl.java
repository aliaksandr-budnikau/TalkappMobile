package talkapp.org.talkappmobile.service.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import talkapp.org.talkappmobile.service.WordsCombinator;

/**
 * @author Budnikau Aliaksandr
 */
public class RandomWordsCombinatorImpl implements WordsCombinator {

    @Override
    public Set<String> combineWords(List<String> words) {
        if (words.isEmpty()) {
            throw new IllegalArgumentException("The list of words is empty");
        }
        if (words.size() == 1) {
            return new LinkedHashSet<>(words);
        }
        if (words.size() == 2) {
            Set<String> pairs = new LinkedHashSet<>();
            pairs.add(words.get(0) + " " + words.get(1));
            return pairs;
        }
        Set<String> pairs = new LinkedHashSet<>();
        Random random = new Random();
        for (int i = words.size() - 1; i > 0; i--) {
            if (i == 1) {
                String pair = words.get(i) + " " + words.get(0);
                pairs.add(pair);
                break;
            }
            String pair = words.get(i) + " " + words.get(random.nextInt(i - 1));
            pairs.add(pair);
        }
        return pairs;
    }
}
