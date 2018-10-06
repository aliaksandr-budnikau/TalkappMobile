package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.model.Sentence;

public interface Word2SentenceCache {
    Sentence findByWord(String word);

    void save(String word, Sentence sentence);
}