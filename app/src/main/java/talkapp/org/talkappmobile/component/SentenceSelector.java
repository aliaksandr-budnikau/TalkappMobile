package talkapp.org.talkappmobile.component;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;

/**
 * @author Budnikau Aliaksandr
 */
public interface SentenceSelector {
    Sentence selectSentence(List<Sentence> sentences);

    void orderByScore(List<Sentence> sentences);
}
