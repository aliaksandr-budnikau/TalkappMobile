package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

public interface SentenceService {
    boolean classifySentence(Sentence sentence);

    List<Sentence> fetchSentencesFromServerByWordAndWordSetId(Word2Tokens word);

    void saveSentences(Map<String, List<Sentence>> words2Sentences, int wordsNumber);

    Map<String, List<Sentence>> findSentencesByWordSetId(int wordSetId, int wordsNumber);
}