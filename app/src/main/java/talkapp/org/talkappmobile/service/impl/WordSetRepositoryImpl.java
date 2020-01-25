package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordSetRepository;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

public class WordSetRepositoryImpl implements WordSetRepository {

    private final WordSetDao wordSetDao;
    private final WordSetMapper wordSetMapper;

    public WordSetRepositoryImpl(WordSetDao wordSetDao, ObjectMapper mapper) {
        this.wordSetDao = wordSetDao;
        this.wordSetMapper = new WordSetMapper(mapper);
    }

    @Override
    public List<WordSet> findAll() {
        return toDtos(wordSetDao.findAll());
    }

    @Override
    public List<WordSet> findAllByTopicId(int topicId) {
        return toDtos(wordSetDao.findAllByTopicId(String.valueOf(topicId)));
    }

    @Override
    public WordSet findById(int wordSetId) {
        WordSetMapping mapping = wordSetDao.findById(wordSetId);
        if (mapping == null) {
            return null;
        }
        return wordSetMapper.toDto(mapping);
    }

    @Override
    public void createNewOrUpdate(WordSet wordSet) {
        wordSetDao.createNewOrUpdate(wordSetMapper.toMapping(wordSet));
    }

    @Override
    public void createNewOrUpdate(List<WordSet> wordSets) {
        LinkedList<WordSetMapping> mappings = new LinkedList<>();
        for (WordSet set : wordSets) {
            mappings.add(wordSetMapper.toMapping(set));
        }
        wordSetDao.refreshAll(mappings);
    }

    @Override
    public void removeById(int wordSetId) {
        wordSetDao.removeById(wordSetId);
    }

    @NonNull
    private List<WordSet> toDtos(List<WordSetMapping> allMappings) {
        List<WordSet> result = new LinkedList<>();
        for (WordSetMapping mapping : allMappings) {
            result.add(wordSetMapper.toDto(mapping));
        }
        return result;
    }
}