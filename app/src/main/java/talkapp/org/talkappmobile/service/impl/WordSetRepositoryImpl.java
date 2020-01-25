package talkapp.org.talkappmobile.service.impl;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.NewWordSetDraftMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;
import talkapp.org.talkappmobile.model.NewWordSetDraft;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.WordSetRepository;
import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

public class WordSetRepositoryImpl implements WordSetRepository {

    private final WordSetDao wordSetDao;
    private final WordSetMapper wordSetMapper;
    private final NewWordSetDraftDao newWordSetDraftDao;
    private int wordSetSize = 12;

    public WordSetRepositoryImpl(WordSetDao wordSetDao, NewWordSetDraftDao newWordSetDraftDao, ObjectMapper mapper) {
        this.wordSetDao = wordSetDao;
        this.newWordSetDraftDao = newWordSetDraftDao;
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

    @Override
    public Integer getTheLastCustomWordSetsId() {
        return wordSetDao.getTheLastCustomWordSetsId();
    }

    @Override
    public NewWordSetDraft getNewWordSetDraft() {
        NewWordSetDraftMapping mapping = newWordSetDraftDao.getNewWordSetDraftById(1);
        if (mapping == null) {
            mapping = new NewWordSetDraftMapping();
            mapping.setWords("");
            return wordSetMapper.toDto(mapping);
        }
        return wordSetMapper.toDto(mapping);
    }

    @Override
    public void createNewOrUpdate(NewWordSetDraft draft) {
        if (draft.getWordTranslations().size() != wordSetSize) {
            throw new RuntimeException("draft.getWordTranslations().size() = " + draft.getWordTranslations().size());
        }
        NewWordSetDraftMapping mapping = wordSetMapper.toMapping(draft);
        newWordSetDraftDao.createNewOrUpdate(mapping);
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