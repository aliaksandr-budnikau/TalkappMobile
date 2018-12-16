package talkapp.org.talkappmobile.component.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.model.Sentence;

import static talkapp.org.talkappmobile.model.SentenceContentScore.POOR;

/**
 * @author Budnikau Aliaksandr
 */
public class RandomSentenceSelectorImpl implements SentenceSelector {

    @Override
    public Sentence selectSentence(List<Sentence> sentences) {
        if (sentences.isEmpty()) {
            throw new IllegalArgumentException("The list of sentences is empty");
        }
        if (sentences.size() == 1) {
            return sentences.get(0);
        }

        LinkedList<Sentence> badSentences = new LinkedList<>();
        LinkedList<Sentence> okSentences = new LinkedList<>();
        LinkedList<Sentence> otherSentences = new LinkedList<>();

        for (Sentence sentence : sentences) {
            if (sentence.getContentScore() == null) {
                okSentences.add(sentence);
            } else if (sentence.getContentScore() == POOR) {
                otherSentences.add(sentence);
            } else {
                badSentences.add(sentence);
            }
        }
        Random random = new Random();
        if (!okSentences.isEmpty()) {
            int i = random.nextInt(okSentences.size());
            return okSentences.get(i);
        }

        if (!otherSentences.isEmpty()) {
            int i = random.nextInt(otherSentences.size());
            return otherSentences.get(i);
        }

        int i = random.nextInt(badSentences.size());
        return badSentences.get(i);
    }

    @Override
    public void orderByScore(List<Sentence> sentences) {
        Collections.sort(sentences, new Comparator<Sentence>() {
            @Override
            public int compare(Sentence o1, Sentence o2) {
                if (o1.getContentScore() == null) {
                    if (o2.getContentScore() == null) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    if (o2.getContentScore() == null) {
                        return 1;
                    }
                }
                return o1.getContentScore().compareTo(o2.getContentScore());
            }
        });
    }
}
