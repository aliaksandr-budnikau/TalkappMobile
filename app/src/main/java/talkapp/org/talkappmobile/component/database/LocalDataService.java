package talkapp.org.talkappmobile.component.database;

import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface LocalDataService {
    List<WordSet> findAllWordSets();

    void saveWordSets(List<WordSet> wordSets);

    List<WordSet> findAllWordSetsByTopicId(int topicId);

    void saveTopics(List<Topic> topics);

    List<Topic> findAllTopics();

    List<Sentence> findSentencesByWords(Word2Tokens word, int wordsNumber);

    List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language);

    void saveWordTranslations(List<WordTranslation> wordTranslations, List<String> words, String language);

    List<String> findWordsOfWordSetById(int wordSetId);

    void saveSentences(Map<String, List<Sentence>> body, int wordsNumber);

    double getOverallExp();
}