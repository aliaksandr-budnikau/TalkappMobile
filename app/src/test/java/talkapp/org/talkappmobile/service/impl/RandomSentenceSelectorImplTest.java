package talkapp.org.talkappmobile.service.impl;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.service.SentenceSelector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Budnikau Aliaksandr
 */
public class RandomSentenceSelectorImplTest {
    private SentenceSelector sentenceSelector = new RandomSentenceSelectorImpl();

    @Test(expected = IllegalArgumentException.class)
    public void getSentence_empty() throws Exception {
        sentenceSelector.getSentence(new ArrayList<Sentence>());
    }

    @Test
    public void getSentence_count1() throws Exception {
        // setup
        List<Sentence> sentences = new ArrayList<>();
        Sentence e = new Sentence();
        sentences.add(e);

        // when
        Sentence sentence = sentenceSelector.getSentence(sentences);

        // then
        assertEquals(e, sentence);
    }

    @Test
    public void getSentence_count2() throws Exception {
        // setup
        List<Sentence> sentences = new ArrayList<>();
        Sentence e1 = new Sentence();
        sentences.add(e1);
        Sentence e2 = new Sentence();
        sentences.add(e2);

        // when
        Sentence sentence = sentenceSelector.getSentence(sentences);

        // then
        assertTrue(sentence == e1 || sentence == e2);
    }
}