package talkapp.org.talkappmobile.component;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;

public interface SentenceProvider {
    List<Sentence> findByWordAndWordSetId(String word, int wordSetId);

    void enableRepetitionMode();

    void disableRepetitionMode();
}