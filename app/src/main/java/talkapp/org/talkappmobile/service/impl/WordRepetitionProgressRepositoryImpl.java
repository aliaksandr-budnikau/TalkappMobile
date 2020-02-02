package talkapp.org.talkappmobile.service.impl;

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

    public WordRepetitionProgressRepositoryImpl(WordRepetitionProgressDao progressDao, ObjectMapper mapper) {
        this.progressDao = progressDao;
        this.sentenceMapper = new SentenceMapper(mapper);
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

    private WordRepetitionProgress toDto(WordRepetitionProgressMapping mapping) {
        WordRepetitionProgress dto = new WordRepetitionProgress();
        if (isEmpty(mapping.getSentenceIds())) {
            dto.setSentenceIds(Collections.<String>emptyList());
        } else {
            dto.setSentenceIds(sentenceMapper.toSentenceId(mapping.getSentenceIds()));
        }
        return dto;
    }
}