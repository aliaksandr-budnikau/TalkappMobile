package talkapp.org.talkappmobile.component;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;

public interface SentenceProvider {
    List<Sentence> findByWord(String word);

    void enableRepetitionMode();

    void disableRepetitionMode();
}