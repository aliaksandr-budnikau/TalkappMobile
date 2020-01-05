package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface LocalDataService {

    void saveTopics(List<Topic> topics);

    List<Topic> findAllTopics();

    List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language);

    void saveSentences(Map<String, List<Sentence>> body, int wordsNumber);
}