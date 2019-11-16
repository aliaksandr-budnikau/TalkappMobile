package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface SentenceService {
    boolean classifySentence(Sentence sentence);

    List<Sentence> fetchSentencesFromServerByWordAndWordSetId(Word2Tokens word);

    List<Sentence> fetchSentencesNotFromServerByWordAndWordSetId(Word2Tokens word);

    List<Sentence> selectSentences(List<Sentence> sentences);

    Sentence convertToSentence(WordTranslation wordTranslation);

    void orderByScore(List<Sentence> sentences);
}