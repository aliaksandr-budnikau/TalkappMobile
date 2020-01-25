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

    @NonNull
    private List<WordSet> toDtos(List<WordSetMapping> allMappings) {
        List<WordSet> result = new LinkedList<>();
        for (WordSetMapping mapping : allMappings) {
            result.add(wordSetMapper.toDto(mapping));
        }
        return result;
    }
}