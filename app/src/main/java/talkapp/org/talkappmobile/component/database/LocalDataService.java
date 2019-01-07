package talkapp.org.talkappmobile.component.database;

import java.util.List;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;

public interface LocalDataService {
    List<WordSet> findAllWordSets();

    void saveWordSets(List<WordSet> wordSets);

    List<WordSet> findAllWordSetsFromMemCache();

    List<WordSet> findAllWordSetsByTopicIdFromMemCache(int topicId);

    List<Topic> findAllTopicsFromMemCache();

    void saveTopics(List<Topic> topics);

    List<Topic> findAllTopics();

    void saveSentences(List<Sentence> body, Word2Tokens words, int wordsNumber);

    List<Sentence> findSentencesByWords(Word2Tokens word, int wordsNumber);

    List<Sentence> findSentencesByWordsFromMemCache(Word2Tokens word, int wordsNumber);
}