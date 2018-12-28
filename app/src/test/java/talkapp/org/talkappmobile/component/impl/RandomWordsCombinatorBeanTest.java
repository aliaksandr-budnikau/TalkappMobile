package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import talkapp.org.talkappmobile.component.WordsCombinator;
import talkapp.org.talkappmobile.model.Word2Tokens;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Budnikau Aliaksandr
 */
public class RandomWordsCombinatorBeanTest {
    private WordsCombinator combinator = new RandomWordsCombinatorBean();

    @Test(expected = IllegalArgumentException.class)
    public void combineWords_empty() throws Exception {
        combinator.combineWords(new ArrayList<Word2Tokens>());
    }

    @Test
    public void combineWords_count1() throws Exception {
        // setup
        ArrayList<Word2Tokens> words = new ArrayList<>();
        words.add(new Word2Tokens("word1"));

        // when
        Set<Word2Tokens> set = combinator.combineWords(words);

        // then
        assertEquals(new HashSet<>(words), set);
    }

    @Test
    public void combineWords_count2() throws Exception {
        // setup
        ArrayList<Word2Tokens> words = new ArrayList<>();
        Word2Tokens word1 = new Word2Tokens("word1");
        Word2Tokens word2 = new Word2Tokens("word2");
        words.add(word1);
        words.add(word2);

        // when
        Set<Word2Tokens> set = combinator.combineWords(words);

        // then
        assertEquals(2, set.size());
        assertTrue(set.contains(word1));
        assertTrue(set.contains(word2));
    }

    @Test
    public void combineWords_count3() throws Exception {
        // setup
        ArrayList<Word2Tokens> words = new ArrayList<>();
        Word2Tokens word1 = new Word2Tokens("word1");
        Word2Tokens word2 = new Word2Tokens("word2");
        Word2Tokens word3 = new Word2Tokens("word3");
        words.add(word1);
        words.add(word2);
        words.add(word3);

        // when
        Set<Word2Tokens> set = combinator.combineWords(words);

        // then
        assertEquals(3, set.size());
        assertTrue(set.contains(word1));
        assertTrue(set.contains(word2));
        assertTrue(set.contains(word3));
    }
}