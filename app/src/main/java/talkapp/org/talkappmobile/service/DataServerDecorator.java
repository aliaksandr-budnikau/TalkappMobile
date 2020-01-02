package talkapp.org.talkappmobile.service;

import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

class DataServerDecorator implements DataServer {

    private final DataServer server;

    public DataServerDecorator(DataServer server) {
        this.server = server;
    }

    public DataServer getServer() {
        return server;
    }

    @Override
    public Map<String, List<Sentence>> findSentencesByWordSetId(int wordSetId, int wordsNumber) {
        return server.findSentencesByWordSetId(wordSetId, wordsNumber);
    }

    @Override
    public List<Topic> findAllTopics() {
        return server.findAllTopics();
    }

    @Override
    public List<WordSet> findAllWordSets() {
        return server.findAllWordSets();
    }

    @Override
    public List<WordSet> findWordSetsByTopicId(int topicId) {
        return server.findWordSetsByTopicId(topicId);
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordSetIdAndByLanguage(int wordSetId, String language) {
        return server.findWordTranslationsByWordSetIdAndByLanguage(wordSetId, language);
    }

    @Override
    public WordTranslation findWordTranslationByWordAndByLanguageAndByLetter(String word, String letter, String language) {
        return server.findWordTranslationByWordAndByLanguageAndByLetter(word, letter, language);
    }

    @Override
    public List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language) {
        return server.findWordTranslationsByWordsAndByLanguage(words, language);
    }

    @Override
    public WordTranslation findWordTranslationsByWordAndByLanguage(String language, String word) {
        return server.findWordTranslationsByWordAndByLanguage(language, word);
    }

    @Override
    public boolean saveSentenceScore(Sentence sentence) {
        return server.saveSentenceScore(sentence);
    }
}