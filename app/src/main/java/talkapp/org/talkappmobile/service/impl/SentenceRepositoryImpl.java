package talkapp.org.talkappmobile.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.service.SentenceRepository;
import talkapp.org.talkappmobile.service.mapper.SentenceMapper;

import static java.util.Collections.singletonList;

public class SentenceRepositoryImpl implements SentenceRepository {

    private final SentenceDao sentenceDao;
    private final SentenceMapper sentenceMapper;

    public SentenceRepositoryImpl(SentenceDao sentenceDao, ObjectMapper mapper) {
        this.sentenceDao = sentenceDao;
        this.sentenceMapper = new SentenceMapper(mapper);
    }

    @Override
    public void createNewOrUpdate(Sentence sentence) {
        SentenceMapping mapping = sentenceMapper.toMapping(sentence);
        sentenceDao.save(singletonList(mapping));
    }

    @Override
    public Sentence findById(String id) {
        SentenceMapping mapping = sentenceDao.findById(id);
        if (mapping == null) {
            return null;
        }
        return sentenceMapper.toDto(mapping);
    }

    @Override
    public List<Sentence> findAllByWord(String word, int wordsNumber) {
        List<SentenceMapping> allByWord = sentenceDao.findAllByWord(word, wordsNumber);
        List<Sentence> result = new LinkedList<>();
        for (SentenceMapping mapping : allByWord) {
            result.add(sentenceMapper.toDto(mapping));
        }
        return result;
    }
}