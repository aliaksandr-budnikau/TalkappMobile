package talkapp.org.talkappmobile.service.impl;

import java.util.List;
import java.util.Random;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.service.SentenceSelector;

/**
 * @author Budnikau Aliaksandr
 */
public class RandomSentenceSelectorImpl implements SentenceSelector {

    @Override
    public Sentence getSentence(List<Sentence> sentences) {
        if (sentences.isEmpty()) {
            throw new IllegalArgumentException("The list of sentences is empty");
        }
        if (sentences.size() == 1) {
            return sentences.get(0);
        }
        int i = new Random().nextInt(sentences.size() - 1);
        return sentences.get(i);
    }
}
