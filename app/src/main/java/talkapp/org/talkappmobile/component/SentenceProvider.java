package talkapp.org.talkappmobile.component;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public interface SentenceProvider {
    List<Sentence> findByWordAndWordSetId(Word2Tokens word, int wordSetId);

    void enableRepetitionMode();

    void disableRepetitionMode();
}