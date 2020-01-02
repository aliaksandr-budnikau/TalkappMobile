package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface DataServer {

    List<Sentence> findSentencesByWords(Word2Tokens words, int wordsNumber, int wordSetId);

    Map<String, List<Sentence>> findSentencesByWordSetId(int wordSetId, int wordsNumber);

    List<Topic> findAllTopics();

    List<WordSet> findAllWordSets();

    List<WordSet> findWordSetsByTopicId(int topicId);

    List<WordTranslation> findWordTranslationsByWordSetIdAndByLanguage(int wordSetId, String language);

    List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language);

    WordTranslation findWordTranslationsByWordAndByLanguage(String language, String word);

    boolean saveSentenceScore(Sentence sentence);

    WordTranslation findWordTranslationByWordAndByLanguageAndByLetter(String word, String letter, String language);
}