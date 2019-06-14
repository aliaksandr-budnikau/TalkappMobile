package talkapp.org.talkappmobile.component.impl;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.SentenceContentScore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static talkapp.org.talkappmobile.model.SentenceContentScore.CORRUPTED;
import static talkapp.org.talkappmobile.model.SentenceContentScore.INSULT;
import static talkapp.org.talkappmobile.model.SentenceContentScore.POOR;

/**
 * @author Budnikau Aliaksandr
 */
public class RandomSentenceSelectorBeanTest {
    private SentenceSelector sentenceSelector = new RandomSentenceSelectorBean();

    @Test(expected = IllegalArgumentException.class)
    public void getSentence_empty() throws Exception {
        sentenceSelector.selectSentences(new ArrayList<Sentence>());
    }

    @Test
    public void getSentence_count1() throws Exception {
        // setup
        List<Sentence> sentences = new ArrayList<>();
        Sentence e = new Sentence();
        sentences.add(e);

        // when
        List<Sentence> actualSentences = sentenceSelector.selectSentences(sentences);

        // then
        assertEquals(sentences, actualSentences);
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
        List<Sentence> sentence = sentenceSelector.selectSentences(sentences);

        // then
        assertTrue(sentence.get(0) == e1 || sentence.get(0) == e2);
    }

    @Test
    public void orderByScore() {
        // setup
        LinkedList<Sentence> sentences = new LinkedList<>();
        for (SentenceContentScore score : SentenceContentScore.values()) {
            Sentence sentence = new Sentence();
            sentence.setContentScore(score);
            sentences.addFirst(sentence);
        }
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());
        sentences.addLast(new Sentence());

        Collections.shuffle(sentences);

        // when
        sentenceSelector.orderByScore(sentences);

        // then
        Iterator<Sentence> iterator = sentences.iterator();
        Sentence sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(null, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(POOR, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(CORRUPTED, sentence.getContentScore());
        sentence = iterator.next();
        assertEquals(INSULT, sentence.getContentScore());
    }
}