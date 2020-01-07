package talkapp.org.talkappmobile.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.impl.InternetConnectionLostException;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

public class CachedWordSetServiceDecorator extends WordSetServiceDecorator {
    private final WordSetDao wordSetDao;
    private final WordSetMapper wordSetMapper;

    public CachedWordSetServiceDecorator(WordSetService wordSetService, WordSetDao wordSetDao, ObjectMapper mapper) {
        super(wordSetService);
        this.wordSetDao = wordSetDao;
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    @Override
    public List<WordSet> getWordSets(Topic topic) {
        List<WordSet> wordSets = null;
        try {
            wordSets = super.getWordSets(topic);
        } catch (InternetConnectionLostException e) {
            return getWordSetsFromDB(topic);
        }
        if (!wordSets.isEmpty()) {
            super.saveWordSets(wordSets);
        }
        return getWordSetsFromDB(topic);
    }

    private List<WordSet> getWordSetsFromDB(Topic topic) {
        if (topic == null) {
            return findAllWordSetsInDB();
        } else {
            return findAllWordSetsInDBByTopicId(topic.getId());
        }
    }

    private List<WordSet> findAllWordSetsInDB() {
        List<WordSetMapping> allMappings = wordSetDao.findAll();
        List<WordSet> result = new LinkedList<>();
        for (WordSetMapping mapping : allMappings) {
            result.add(wordSetMapper.toDto(mapping));
        }
        return result;
    }

    private List<WordSet> findAllWordSetsInDBByTopicId(int topicId) {
        List<WordSetMapping> allMappings = wordSetDao.findAllByTopicId(String.valueOf(topicId));
        List<WordSet> result = new LinkedList<>();
        for (WordSetMapping mapping : allMappings) {
            result.add(wordSetMapper.toDto(mapping));
        }
        return result;
    }
}