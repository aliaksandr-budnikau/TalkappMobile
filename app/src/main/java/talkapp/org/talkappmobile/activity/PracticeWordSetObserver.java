package talkapp.org.talkappmobile.activity;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;

public interface PracticeWordSetObserver {
    void onInitialise(WordSet wordSet);

    void onSentencesNotFound(String words);

    void onNextSentence(Sentence sentence) throws InterruptedException;

    void onFinish();

    void onInterruption();
}