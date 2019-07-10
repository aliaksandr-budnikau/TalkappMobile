package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;

import java.util.List;

public interface SentenceService {
    boolean classifySentence(Sentence sentence);

    List<Sentence> fetchSentencesFromServerByWordAndWordSetId(Word2Tokens word);

    List<Sentence> fetchSentencesNotFromServerByWordAndWordSetId(Word2Tokens word);

    List<Sentence> selectSentences(List<Sentence> sentences);

    void orderByScore(List<Sentence> sentences);
}