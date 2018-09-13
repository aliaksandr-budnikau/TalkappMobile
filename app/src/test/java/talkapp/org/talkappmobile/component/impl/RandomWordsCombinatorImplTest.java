package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import talkapp.org.talkappmobile.component.WordsCombinator;

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
        String word1 = "word1";
        String word2 = "word2";
        words.add(word1);
        words.add(word2);

        // when
        Set<String> set = combinator.combineWords(words);

        // then
        assertEquals(2, set.size());
        assertTrue(set.contains(word1));
        assertTrue(set.contains(word2));
    }

    @Test
    public void combineWords_count3() throws Exception {
        // setup
        ArrayList<String> words = new ArrayList<>();
        String word1 = "word1";
        String word2 = "word2";
        String word3 = "word3";
        words.add(word1);
        words.add(word2);
        words.add(word3);

        // when
        Set<String> set = combinator.combineWords(words);

        // then
        assertEquals(3, set.size());
        assertTrue(set.contains(word1));
        assertTrue(set.contains(word2));
        assertTrue(set.contains(word3));
    }
}