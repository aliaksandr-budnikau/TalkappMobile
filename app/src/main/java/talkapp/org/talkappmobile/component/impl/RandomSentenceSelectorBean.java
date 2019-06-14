package talkapp.org.talkappmobile.component.impl;

import org.androidannotations.annotations.EBean;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.component.SentenceSelector;
import talkapp.org.talkappmobile.model.Sentence;

import static talkapp.org.talkappmobile.model.SentenceContentScore.POOR;

/**
 * @author Budnikau Aliaksandr
 */
@EBean(scope = EBean.Scope.Singleton)
public class RandomSentenceSelectorBean implements SentenceSelector {

    @Override
    public List<Sentence> selectSentences(List<Sentence> sentences) {
        if (sentences.isEmpty()) {
            throw new IllegalArgumentException("The list of sentences is empty");
        }
        if (sentences.size() == 1) {
            return sentences;
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
        if (!okSentences.isEmpty()) {
            return okSentences;
        }

        if (!otherSentences.isEmpty()) {
            return otherSentences;
        }

        return badSentences;
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
