package talkapp.org.talkappmobile.component.backend;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface DataServer {

    List<Sentence> findSentencesByWords(Word2Tokens words, int wordsNumber, int wordSetId);

    void initLocalCacheOfAllSentencesForThisWordset(int wordSetId, int wordsNumber);

    void initLocalCacheOfAllSentencesForThisWord(String word, int wordsNumber);

    List<Topic> findAllTopics();

    List<WordSet> findAllWordSets();

    List<WordSet> findWordSetsByTopicId(int topicId);

    List<WordTranslation> findWordTranslationsByWordSetIdAndByLanguage(int wordSetId, String language);

    List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language);

    WordTranslation findWordTranslationsByWordAndByLanguage(String language, String word);

    boolean saveSentenceScore(Sentence sentence);

    WordSet saveNewCustomWordSet(WordSet wordSet);
}