package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.model.WordRepetitionProgress;
import talkapp.org.talkappmobile.service.WordRepetitionProgressRepository;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class WordRepetitionProgressRepositoryImpl implements WordRepetitionProgressRepository {
    private final WordRepetitionProgressDao progressDao;
    private final SentenceMapper sentenceMapper;
    private final ObjectMapper mapper;

    public WordRepetitionProgressRepositoryImpl(WordRepetitionProgressDao progressDao, ObjectMapper mapper) {
        this.progressDao = progressDao;
        this.sentenceMapper = new SentenceMapper(mapper);
        this.mapper = mapper;
    }

    @Override
    public List<WordRepetitionProgress> findByWordIndexAndWordSetId(int index, Integer sourceWordSetId) {
        List<WordRepetitionProgressMapping> mappings = progressDao.findByWordIndexAndWordSetId(index, sourceWordSetId);
        LinkedList<WordRepetitionProgress> result = new LinkedList<>();
        for (WordRepetitionProgressMapping mapping : mappings) {
            result.add(toDto(mapping));
        }
        return result;
    }

    @Override
    public void createNewOrUpdate(WordRepetitionProgress progress) {
        progressDao.createNewOrUpdate(toMapping(progress));
    }

    private WordRepetitionProgressMapping toMapping(WordRepetitionProgress progress) {
        WordRepetitionProgressMapping mapping = new WordRepetitionProgressMapping();
        try {
            mapping.setSentenceIds(mapper.writeValueAsString(progress.getSentenceIds()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        mapping.setUpdatedDate(progress.getUpdatedDate());
        mapping.setRepetitionCounter(progress.getRepetitionCounter());
        mapping.setWordSetId(progress.getWordSetId());
        mapping.setWordIndex(progress.getWordIndex());
        mapping.setForgettingCounter(progress.getForgettingCounter());
        mapping.setId(progress.getId());
        mapping.setStatus(progress.getStatus());
        return mapping;
    }

    private WordRepetitionProgress toDto(WordRepetitionProgressMapping mapping) {
        WordRepetitionProgress dto = new WordRepetitionProgress();
        if (isEmpty(mapping.getSentenceIds())) {
            dto.setSentenceIds(Collections.<String>emptyList());
        } else {
            dto.setSentenceIds(sentenceMapper.toSentenceId(mapping.getSentenceIds()));
        }
        dto.setUpdatedDate(mapping.getUpdatedDate());
        dto.setRepetitionCounter(mapping.getRepetitionCounter());
        dto.setWordSetId(mapping.getWordSetId());
        dto.setWordIndex(mapping.getWordIndex());
        dto.setForgettingCounter(mapping.getForgettingCounter());
        dto.setId(mapping.getId());
        dto.setStatus(mapping.getStatus());
        return dto;
    }
}