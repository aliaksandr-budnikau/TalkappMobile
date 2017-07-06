package talkapp.org.talkappmobile.service.impl;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;

/**
 * @author Budnikau Aliaksandr
 */
public interface GameProcessCallback {
    void returnProgress(Sentence sentence);

    List<Sentence> findByWords(String words);
}