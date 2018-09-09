package talkapp.org.talkappmobile.service.impl;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSetExperience;

/**
 * @author Budnikau Aliaksandr
 */
public interface GameProcessCallback {
    void returnProgress(Sentence sentence) throws InterruptedException;

    WordSetExperience createExperience(String wordSetId);

    List<Sentence> findByWords(String words);

    void onFinish();

    void onInterruption();
}