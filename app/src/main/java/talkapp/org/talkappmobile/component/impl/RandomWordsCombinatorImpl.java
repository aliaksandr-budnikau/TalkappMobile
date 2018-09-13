package talkapp.org.talkappmobile.component.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.component.WordsCombinator;

/**
 * @author Budnikau Aliaksandr
 */
public class RandomWordsCombinatorImpl implements WordsCombinator {

    @Override
    public Set<String> combineWords(List<String> words) {
        if (words.isEmpty()) {
            throw new IllegalArgumentException("The list of words is empty");
        }
        ArrayList<String> sequence = new ArrayList<>(words);
        Collections.shuffle(sequence);
        return new LinkedHashSet<>(sequence);
    }
}
