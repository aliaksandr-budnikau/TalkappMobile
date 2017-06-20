package talkapp.org.talkappmobile.service.impl;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import talkapp.org.talkappmobile.service.WordsCombinator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Budnikau Aliaksandr
 */
public class RandomWordsCombinatorImplTest {
    private WordsCombinator combinator = new RandomWordsCombinatorImpl();

    @Test(expected = IllegalArgumentException.class)
    public void combineWords_empty() throws Exception {
        combinator.combineWords(new ArrayList<String>());
    }

    @Test
    public void combineWords_count1() throws Exception {
        // setup
        ArrayList<String> words = new ArrayList<>();
        words.add("word1");

        // when
        Set<String> set = combinator.combineWords(words);

        // then
        assertEquals(new HashSet<>(words), set);
    }

    @Test
    public void combineWords_count2() throws Exception {
        // setup
        ArrayList<String> words = new ArrayList<>();
        words.add("word1");
        words.add("word2");

        // when
        Set<String> set = combinator.combineWords(words);

        // then
        assertEquals(words.get(0) + " " + words.get(1), set.iterator().next());
    }

    @Test
    public void combineWords_count3() throws Exception {
        // setup
        ArrayList<String> words = new ArrayList<>();
        words.add("word1");
        words.add("word2");
        words.add("word3");

        // when
        Set<String> set = combinator.combineWords(words);

        // then
        Iterator<String> iterator = set.iterator();
        String first = iterator.next();
        assertTrue((words.get(2) + " " + words.get(1)).equals(first) || (words.get(2) + " " + words.get(0)).equals(first));
        String second = iterator.next();
        assertTrue((words.get(1) + " " + words.get(0)).equals(second));
    }
}