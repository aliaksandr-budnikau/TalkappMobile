package talkapp.org.talkappmobile.component;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;

/**
 * @author Budnikau Aliaksandr
 */
public interface SentenceSelector {
    List<Sentence> selectSentences(List<Sentence> sentences);

    void orderByScore(List<Sentence> sentences);
}
