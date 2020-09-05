package talkapp.org.talkappmobile.repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.mappings.SentenceMapping;
import talkapp.org.talkappmobile.model.Sentence;

import static java.util.Collections.singletonList;

public class SentenceRepositoryImpl implements SentenceRepository {

    private final SentenceDao sentenceDao;
    private final SentenceMapper sentenceMapper;

    @Inject
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

    @Override
    public List<Sentence> findAllByIds(String[] ids) {
        List<SentenceMapping> mappings = sentenceDao.findAllByIds(ids);
        LinkedList<Sentence> result = new LinkedList<>();
        for (SentenceMapping mapping : mappings) {
            result.add(sentenceMapper.toDto(mapping));
        }
        return result;
    }
}