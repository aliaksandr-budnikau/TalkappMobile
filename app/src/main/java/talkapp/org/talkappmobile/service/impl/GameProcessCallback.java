package talkapp.org.talkappmobile.service.impl;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;

/**
 * @author Budnikau Aliaksandr
 */
public interface GameProcessCallback {
    void returnProgress(Sentence sentence) throws InterruptedException;

    List<Sentence> findByWords(String words);

    void onFinish();

    void onInterruption();
}