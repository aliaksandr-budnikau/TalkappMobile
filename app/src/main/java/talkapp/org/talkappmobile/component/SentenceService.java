package talkapp.org.talkappmobile.component;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public interface SentenceService {
    boolean classifySentence(Sentence sentence);

    List<Sentence> fetchSentencesFromServerByWordAndWordSetId(Word2Tokens word, int wordSetId);

    List<Sentence> fetchSentencesNotFromServerByWordAndWordSetId(Word2Tokens word, int wordSetId);
}